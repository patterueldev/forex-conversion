import SwiftUI
import ForexMobileCore

// Protocol that abstracts ForexMobileService functionality
protocol ForexServiceProtocol {
    func fetchOnStartUpOnce() async throws
    func fetchAndStoreLatestRate() async throws
    func getBaseURL() -> String
    func setBaseURL(url: String)
    func getCachedRate(from: String, to: String) async throws -> (rate: String, updatedAt: String)
    func convertCurrency(amount: Double, from: String, to: String) async throws -> Converted
    func toggleSimulatedNetwork(shouldSimulate: Bool)
    func isSimulatedOffline() -> Bool
}

// Real implementation that wraps ForexMobileService
class RealForexService: ForexServiceProtocol {
    private let service = ForexMobileService()
    
    func fetchOnStartUpOnce() async throws {
        try await service.fetchOnStartUpOnce()
    }
    
    func fetchAndStoreLatestRate() async throws {
        try await service.fetchAndStoreLatestRate()
    }
    
    func getBaseURL() -> String {
        service.getBaseURL()
    }
    
    func setBaseURL(url: String) {
        service.setBaseURL(url: url)
    }
    
    func getCachedRate(from: String, to: String) async throws -> (rate: String, updatedAt: String) {
        let cachedRate = try await service.getCachedRate(from: service.getCurrencyUSD(), to: service.getCurrencyPHP())
        return (String(cachedRate.rate.amount), cachedRate.formattedUpdatedAt)
    }
    
    func convertCurrency(amount: Double, from: String, to: String) async throws -> Converted {
        // We only support USD -> PHP for now
        let result = try await service.convertCurrency(amount: amount, from: service.getCurrencyUSD(), to: service.getCurrencyPHP())
        return result
    }
    
    func toggleSimulatedNetwork(shouldSimulate: Bool) {
        service.toggleSimulatedNetwork(shouldSimulate: shouldSimulate)
    }
    
    func isSimulatedOffline() -> Bool {
        service.isSimulatedOffline()
    }
}

// Type alias to avoid confusion
typealias ForexMobileCoreCurrency = Currency

// Mock implementation for preview support
class MockForexService: ForexServiceProtocol {
    private var cachedRates: [String: String] = [:]
    private var baseUrl: String = "http://localhost:8080"
    private var simulatedOffline: Bool = false
    private var lastUpdatedString: String = "Feb 11, 2026, 10:00 PM"
    
    func fetchOnStartUpOnce() async throws {
        cachedRates["USD-PHP"] = "56.78"
    }
    
    func fetchAndStoreLatestRate() async throws {
        cachedRates["USD-PHP"] = "56.78"
    }
    
    func getBaseURL() -> String {
        baseUrl
    }
    
    func setBaseURL(url: String) {
        baseUrl = url
    }
    
    func getCachedRate(from: String, to: String) async throws -> (rate: String, updatedAt: String) {
        let key = "\(from)-\(to)"
        return (cachedRates[key] ?? "56.78", lastUpdatedString)
    }
    
    func convertCurrency(amount: Double, from: String, to: String) async throws -> Converted {
        let rate = Double(cachedRates["USD-PHP"] ?? "56.78") ?? 56.78
        return Converted(
            rate: Rate(
                fromCurrency: .usd,
                toCurrency: .php,
                amount: rate
            ),
            originalAmount: amount,
            convertedAmount: amount * rate
        )
    }
    
    func toggleSimulatedNetwork(shouldSimulate: Bool) {
        simulatedOffline = shouldSimulate
    }
    
    func isSimulatedOffline() -> Bool {
        simulatedOffline
    }
}

@MainActor
class OfflineDemoViewModel: ObservableObject {
    @Published var simulateOffline: Bool = false {
        didSet {
            forexService.toggleSimulatedNetwork(shouldSimulate: simulateOffline)
        }
    }
    @Published var isLoading: Bool = true
    @Published var latestStoredRate: String = "-"
    @Published var rateLastUpdatedTime: String?
    @Published var convertedAmount: String = "-"
    @Published var amountText: String = "1.0"
    
    // Base URL for fetching; editable via modal
    @Published var baseURL: String = "" {
        didSet {
            if !baseURL.isEmpty {
                forexService.setBaseURL(url: baseURL)
            }
        }
    }
    @Published var showingBaseURLModal: Bool = false
    
    // Last conversion result for navigation
    @Published var lastConversionResult: Converted?
    
    private var hasCalledFetchOnStartUp = false
    let forexService: ForexServiceProtocol

    init(forexService: ForexServiceProtocol) {
        self.forexService = forexService
        self.baseURL = forexService.getBaseURL()
    }

    func fetchOnStartUpOnce() async {
        guard !hasCalledFetchOnStartUp else { return }
        hasCalledFetchOnStartUp = true
        
        do {
            try await forexService.fetchOnStartUpOnce()
            await loadStoredRate()
        } catch {
            latestStoredRate = "error"
        }
        
        isLoading = false
    }

    func fetchLatestRateAndSave() async {
        isLoading = true
        do {
            try await forexService.fetchAndStoreLatestRate()
            await loadStoredRate()
        } catch {
            latestStoredRate = "error"
        }
        isLoading = false
    }

    func loadStoredRate() async {
        do {
            let (rate, updatedAtString) = try await forexService.getCachedRate(from: "USD", to: "PHP")
            latestStoredRate = rate
            rateLastUpdatedTime = updatedAtString
        } catch {
            latestStoredRate = "error"
            rateLastUpdatedTime = nil
        }
    }

    func convert() async {
        isLoading = true
        do {
            let amount = Double(amountText) ?? 0.0
            let converted = try await forexService.convertCurrency(amount: amount, from: "USD", to: "PHP")
            lastConversionResult = converted
            convertedAmount = "\(simulateOffline ? "offline" : "online") | \(converted.originalAmount) \(converted.rate.fromCurrency) -> \(String(format: "%.2f", converted.convertedAmount)) \(converted.rate.toCurrency) @ rate=\(String(format: "%.2f", converted.rate.amount))"
        } catch {
            convertedAmount = "error"
        }
        isLoading = false
    }
    
    func resetBaseURL() {
        baseURL = forexService.getBaseURL()
    }
}

struct OfflineDemoView: View {
    @StateObject private var vm = OfflineDemoViewModel(forexService: RealForexService())
    @EnvironmentObject var iosNavigator: iOSNavigator

    var body: some View {
        ZStack {
            NavigationView {
                Form {
                    Section(header: Text("Mode")) {
                        Toggle("Simulate Offline Mode", isOn: $vm.simulateOffline)
                    }

                    Section(header: Text("Currencies")) {
                        HStack {
                            Text("From")
                            Spacer()
                            Text("USD").font(.body.bold())
                        }
                        HStack {
                            Text("To")
                            Spacer()
                            Text("PHP").font(.body.bold())
                        }
                    }

                    Section(header: Text("Amount")) {
                        TextField("Amount", text: $vm.amountText)
                            .keyboardType(.decimalPad)
                    }

                    Section(header: Text("Rates")) {
                        HStack {
                            Text("Latest Cached Rate:")
                            Spacer()
                            Text(vm.latestStoredRate)
                        }
                        if let updatedTime = vm.rateLastUpdatedTime {
                            HStack {
                                Text("Updated:")
                                    .font(.caption)
                                Spacer()
                                Text(updatedTime)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }

                    Section {
                        Button("Fetch & Store Latest Rate") {
                            Task { await vm.fetchLatestRateAndSave() }
                        }
                        Button("Convert") {
                            Task { await vm.convert() }
                        }
                    }

                    Section(header: Text("Output")) {
                        Button(action: {
                            if let result = vm.lastConversionResult {
                                let conversionResult = ConversionResult(
                                    status: vm.simulateOffline ? .offline : .online,
                                    fromCurrency: "USD",
                                    toCurrency: "PHP",
                                    inputAmount: result.originalAmount,
                                    convertedAmount: result.convertedAmount
                                )
                                iosNavigator.navigate(with: conversionResult)
                            }
                        }) {
                            Text(vm.convertedAmount)
                                .foregroundColor(.blue)
                                .font(.system(.body, design: .monospaced))
                        }
                        .disabled(vm.lastConversionResult == nil)
                    }
                }
                .navigationTitle("Offline/Online Demo")
                .onAppear {
                    // Fetch and store latest rate on first app run (only once)
                    Task {
                        await vm.fetchOnStartUpOnce()
                    }
                }
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: { vm.showingBaseURLModal = true }) {
                            Image(systemName: "gearshape")
                        }
                    }
                }
            }
            .sheet(isPresented: $vm.showingBaseURLModal) {
                NavigationView {
                    Form {
                        Section(header: Text("Base URL")) {
                            TextField("Base URL", text: $vm.baseURL)
                                .keyboardType(.URL)
                                .autocapitalization(.none)
                        }
                        Section {
                            Button("Reset to Default") {
                                vm.resetBaseURL()
                            }
                        }
                    }
                    .navigationTitle("Settings")
                    .toolbar {
                        ToolbarItem(placement: .confirmationAction) {
                            Button("Done") { vm.showingBaseURLModal = false }
                        }
                    }
                }
            }
            
            // Loading overlay - displays during any async operation
            if vm.isLoading {
                ZStack {
                    Color.black.opacity(0.4)
                        .ignoresSafeArea()
                    
                    VStack(spacing: 16) {
                        ProgressView()
                            .scaleEffect(1.5, anchor: .center)
                        Text("Processing...")
                            .font(.headline)
                            .foregroundColor(.white)
                    }
                    .padding()
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(12)
                }
                .ignoresSafeArea()
            }
        }
    }
}

struct OfflineDemoView_Previews: PreviewProvider {
    static var previews: some View {
        let mockService = MockForexService()
        Task {
            try? await mockService.fetchOnStartUpOnce()
        }
        
        return OfflineDemoView()
            .environmentObject(OfflineDemoViewModel(forexService: mockService))
            .environmentObject(iOSNavigator())
    }
}
