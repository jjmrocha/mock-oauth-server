package net.uiqui.oauth.mock.tools

internal class Holder<T>(private var storedValue: T) {
    var value: T
        @Synchronized get(): T = storedValue

        @Synchronized set(newValue: T) {
            storedValue = newValue
        }
}
