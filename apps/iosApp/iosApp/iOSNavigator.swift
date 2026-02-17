//
//  iOSNavigator.swift
//  iosApp
//
//  Created by John Patrick Teruel on 2/17/26.
//

import SwiftUI
import ForexMobileCore

@MainActor
class iOSNavigator: ObservableObject {
    private let navigator = IosResultScreenNavigator()
    var navigationVC: UINavigationController = UINavigationController()
    
    func navigate(with result: ConversionResult) {
        // usage: navigate(with: .init(status: .offline, fromCurrency: "USD", toCurrency: "PHP", inputAmount: 69, convertedAmount: 420))
        navigator.navigate(result: result, parentViewController: navigationVC)
    }
}
