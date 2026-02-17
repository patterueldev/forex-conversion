import SwiftUI
import ForexMobileCore

@main
struct iOSApp: App {
    @StateObject private var iosNavigator = iOSNavigatorEnvironment()
    
    var body: some Scene {
        WindowGroup {
            NavigationControllerRepresentable(
                rootViewController: {
                    let hostingController = UIHostingController(
                        rootView: OfflineDemoView()
                            .environmentObject(iosNavigator.navigator)
                    )
                    hostingController.title = "Forex Converter"
                    return hostingController
                }(),
                navigationVC: iosNavigator.navigationController
            )
            .ignoresSafeArea()
        }
    }
}

// Environment object wrapper to share iOSNavigator through SwiftUI
@MainActor
class iOSNavigatorEnvironment: ObservableObject {
    let navigationController: UINavigationController
    let navigator: iOSNavigator
    
    init() {
        self.navigationController = UINavigationController()
        self.navigator = iOSNavigator()
        self.navigator.navigationVC = self.navigationController
    }
}

// UIViewControllerRepresentable to wrap UINavigationController
struct NavigationControllerRepresentable: UIViewControllerRepresentable {
    let rootViewController: UIViewController
    let navigationVC: UINavigationController
    
    func makeUIViewController(context: Context) -> UINavigationController {
        navigationVC.viewControllers = [rootViewController]
        navigationVC.navigationBar.prefersLargeTitles = true
        return navigationVC
    }
    
    func updateUIViewController(_ uiViewController: UINavigationController, context: Context) {
        // Update logic if needed
    }
}
