import ZIPFoundation
import Foundation
import Cordova

@objc(Unzip) class Unzip: CDVPlugin {

    @objc(unpack:)
    func unpack(command: CDVInvokedUrlCommand) {
        guard let source = command.argument(at: 0) as? String,
              let destination = command.argument(at: 1) as? String else {
            let result = CDVPluginResult(status: .error, messageAs: "Invalid arguments")
            self.commandDelegate.send(result, callbackId: command.callbackId)
            return
        }

        let srcPath = source.replacingOccurrences(of: "file://", with: "")
        let dstPath = destination.replacingOccurrences(of: "file://", with: "")
        let srcURL = URL(fileURLWithPath: srcPath)
        let dstURL = URL(fileURLWithPath: dstPath)

        DispatchQueue.global(qos: .utility).async {
            do {
                guard let archive = Archive(url: srcURL, accessMode: .read) else {
                    throw NSError(domain: "Unzip", code: 1, userInfo: nil)
                }

                try archive.extractAll(to: dstURL)

                let result = CDVPluginResult(status: .ok)
                self.commandDelegate.send(result, callbackId: command.callbackId)

            } catch {
                let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                self.commandDelegate.send(result, callbackId: command.callbackId)
            }
        }
    }
}

extension Archive {
    func extractAll(to destination: URL) throws {
        for entry in self {
            let entryURL = destination.appendingPathComponent(entry.path)
            _ = try self.extract(entry, to: entryURL)
        }
    }
}
