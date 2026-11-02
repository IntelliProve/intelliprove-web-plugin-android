# General Info

You have two main parts in the folder:
1. IntelliProveWebView
2. app

The rest are Android config files which nobody actually understands, or do they...? 🧐

---

The `IntelliProveWebView` folder is the actual SDK. Deep down its folder structure you'll find the `IntelliWebViewActivity.kt` file. It's at "*./IntelliProveWebView/src/main/java/com/intelliprove/webview/IntelliWebViewActivity.kt*".

Here's a GPT barf of what that file does:

## IntelliWebViewActivity – Functional Summary

**Purpose**  
An Android `Activity` that hosts a `WebView` to load a web app, handle JS communication, camera permissions, and external links.

**Key Components**  
- **`IntelliWebViewDelegate`** – callback interface for receiving messages from the web app.  
- **`IntelliWebViewDelegateHolder`** – singleton holder for the delegate.  
- **`IntelliWebViewActivity`**  
  - Configures `WebView` (JavaScript, DOM storage, debugging).  
  - Injects JS to disable zoom and patch `window.postMessage`.  
  - Handles external links via browser intents.  
  - Manages camera permission requests for `getUserMedia`.  
  - Cleans up `WebView` on destroy.  
- **`IntelliWebAppInterface`** – JS bridge that forwards `postMessage` calls to the delegate and dismisses the activity if `"stage": "dismiss"`.

**Flow**  
1. `start(context, url, delegate)` launches activity with URL.  
2. WebView loads the page and patches JS.  
3. `postMessage` events are forwarded to Android.  
4. Camera permission requests go through Android’s system.  
5. External links open in browser.  
6. Delegate is notified of all messages.  

---

The `app` folder is an example implementation that uses the SDK. If you want to test a URL, add it to the `MainActivity.kt` file. You can find it at "*./app/src/main/java/com/intelliprove/MainActivity.kt*".

It's a HelloWorld app with a single button that opens the IntelliProveWebViewActivity, given a specified URL.
To test some code you have running locally, proxy it to a public HTTPS URL and use that one, including the action token.

# Building

You need the following prerequisites:
- Android SDK Build Tools 35.0.0
- Android API 35 -> Android SDK Platform 35

## Building the SDK
When building the SDK, always verify that the Build Variants are set to "Release".

## Running the test app
Set the Build Variants to Debug to fix issues concerning signing of the app.
## Testing on an emulator
To check the scan screen survives Android killing its WebView (the low-memory crash), type `chrome://kill` into the test app's URL field and press the button. Pass: the log shows `Render process gone (crashed: false)` and a `dismiss` message under `MainActivity`, `adb shell pidof com.intelliprove` returns the same id before and after, and the log has no `killing application` line. `chrome://crash` tests a renderer crash the same way.

For the full procedure, including a Cordova host app, see [Testing on an emulator](https://github.com/IntelliProve/intelliprove-web-plugin-cordova/blob/main/docs/emulator-testing.md) in the Cordova plugin repo.
