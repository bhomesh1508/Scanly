package com.docscanner.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.docscanner.app.presentation.common.AppLockGate
import com.docscanner.app.presentation.editor.EditorScreen
import com.docscanner.app.presentation.editor.EditorViewModel
import com.docscanner.app.presentation.folders.FolderDetailScreen
import com.docscanner.app.presentation.folders.FolderDetailViewModel
import com.docscanner.app.presentation.folders.FoldersScreen
import com.docscanner.app.presentation.folders.FoldersViewModel
import com.docscanner.app.presentation.home.HomeScreen
import com.docscanner.app.presentation.home.HomeViewModel
import com.docscanner.app.presentation.scanner.ScannerScreen
import com.docscanner.app.presentation.scanner.ScannerViewModel
import com.docscanner.app.presentation.search.SearchScreen
import com.docscanner.app.presentation.search.SearchViewModel
import com.docscanner.app.presentation.settings.SettingsScreen
import com.docscanner.app.presentation.settings.SettingsViewModel
import com.docscanner.app.presentation.trash.TrashScreen
import com.docscanner.app.presentation.trash.TrashViewModel
import com.docscanner.app.presentation.viewer.ViewerScreen
import com.docscanner.app.presentation.viewer.ViewerViewModel

/**
 * Root navigation composable that wires all screens together with Hilt ViewModels.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomBarRoutes = listOf(
        Screen.Scanner.route,
        Screen.Editor.route,
        Screen.Viewer.route
    )
    val shouldShowBottomBar = currentRoute !in hideBottomBarRoutes

    AppLockGate(isEnabled = false) {
        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (shouldShowBottomBar) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.Scanner.route) }
                    ) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = "Scan Document")
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Home
                composable(Screen.Home.route) {
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToViewer = { docId ->
                            navController.navigate(Screen.Viewer.createRoute(docId))
                        },
                        onNavigateToScanner = {
                            navController.navigate(Screen.Scanner.route)
                        }
                    )
                }

                // Scanner
                composable(Screen.Scanner.route) {
                    val viewModel: ScannerViewModel = hiltViewModel()
                    ScannerScreen(
                        viewModel = viewModel,
                        onNavigateToEditor = { docId ->
                            navController.navigate(Screen.Editor.createRoute(docId)) {
                                popUpTo(Screen.Scanner.route) { inclusive = true }
                            }
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Editor
                composable(
                    route = Screen.Editor.route,
                    arguments = listOf(navArgument("documentId") { type = NavType.StringType })
                ) {
                    val viewModel: EditorViewModel = hiltViewModel()
                    EditorScreen(
                        viewModel = viewModel,
                        onNavigateToViewer = { docId ->
                            navController.navigate(Screen.Viewer.createRoute(docId)) {
                                popUpTo(Screen.Home.route)
                            }
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Viewer
                composable(
                    route = Screen.Viewer.route,
                    arguments = listOf(navArgument("documentId") { type = NavType.StringType })
                ) {
                    val viewModel: ViewerViewModel = hiltViewModel()
                    ViewerScreen(
                        viewModel = viewModel,
                        onNavigateToEditor = { docId ->
                            navController.navigate(Screen.Editor.createRoute(docId))
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Folders
                composable(Screen.Folders.route) {
                    val viewModel: FoldersViewModel = hiltViewModel()
                    FoldersScreen(
                        viewModel = viewModel,
                        onNavigateToFolder = { folderId ->
                            navController.navigate(Screen.FolderDetail.createRoute(folderId))
                        }
                    )
                }

                // Folder Detail
                composable(
                    route = Screen.FolderDetail.route,
                    arguments = listOf(navArgument("folderId") { type = NavType.StringType })
                ) {
                    val viewModel: FolderDetailViewModel = hiltViewModel()
                    FolderDetailScreen(
                        viewModel = viewModel,
                        onNavigateToViewer = { docId ->
                            navController.navigate(Screen.Viewer.createRoute(docId))
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Search
                composable(Screen.Search.route) {
                    val viewModel: SearchViewModel = hiltViewModel()
                    SearchScreen(
                        viewModel = viewModel,
                        onNavigateToViewer = { docId ->
                            navController.navigate(Screen.Viewer.createRoute(docId))
                        }
                    )
                }

                // Settings
                composable(Screen.Settings.route) {
                    val viewModel: SettingsViewModel = hiltViewModel()
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToTrash = { navController.navigate(Screen.Trash.route) }
                    )
                }

                // Trash
                composable(Screen.Trash.route) {
                    val viewModel: TrashViewModel = hiltViewModel()
                    TrashScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
