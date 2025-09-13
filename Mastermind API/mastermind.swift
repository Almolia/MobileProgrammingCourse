//
//  MastermindGame.swift
//
//  Made by Ali M. Shabestari
//  For: Mobile Programming
//
//  This is a simple terminal version of the Mastermind game using a public API.
//  You try to guess a 4-digit secret code where each digit is from 1 to 6.
//  Digits must be unique. Therefore, your guess must not contain repeatative digits.
//  The server gives you feedback: how many digits are correct and in the right spot (black),
//  and how many are correct but in the wrong spot (white).
//
//  To win, you gotta get 4 blacks 👀
//  You can exit anytime by typing 'exit'.
//


import Foundation

// starts the game by hitting the API to get a game ID
func startGame(completion: @escaping (String?) -> Void) {
    var request = URLRequest(url: URL(string: "https://mastermind.darkube.app/game")!, timeoutInterval: .infinity)
    request.addValue("application/json", forHTTPHeaderField: "Accept")
    request.httpMethod = "POST"

    let task = URLSession.shared.dataTask(with: request) { data, _, error in
        // check if we got data and no error
        guard let data = data, error == nil else {
            completion(nil)
            return
        }

        // try to grab game_id from response
        if let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
           let gameId = json["game_id"] as? String {
            completion(gameId)
        } else {
            completion(nil)
        }
    }

    task.resume()
}

// sends a guess to the server, gets number of black/white pins
func sendGuess(gameId: String, guess: String, completion: @escaping (Int?, Int?) -> Void) {
    let url = URL(string: "https://mastermind.darkube.app/guess")!
    var request = URLRequest(url: url, timeoutInterval: .infinity)
    request.httpMethod = "POST"

    // API headers
    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
    request.addValue("application/json", forHTTPHeaderField: "Accept")

    // make the body of the request
    let parameters = """
    {
      "game_id": "\(gameId)",
      "guess": "\(guess)"
    }
    """
    request.httpBody = parameters.data(using: .utf8)

    // hit the API and handle response
    let task = URLSession.shared.dataTask(with: request) { data, _, error in
        guard let data = data, error == nil else {
            print("❌ error sending guess: \(String(describing: error))")
            completion(nil, nil)
            return
        }

        // try to parse black and white from the response
        if let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
           let black = json["black"] as? Int,
           let white = json["white"] as? Int {
            completion(black, white)
        } else {
            print("⚠️ couldn't read server response: \(String(data: data, encoding: .utf8) ?? "")")
            completion(nil, nil)
        }
    }

    task.resume()
}

// asks the user for input and keeps the loop going
func promptGuess(gameId: String) {
    print("\n🔢 Your guess:")
    guard let input = readLine(), input.lowercased() != "exit" else {
        print("👋 peace out")
        exit(0)
    }

    // check: exactly 4 digits, each from 1 to 6
    guard input.count == 4, input.allSatisfy({ "123456".contains($0) }) else {
        print("⚠️ guess must be 4 digits, only from 1 to 6 bro")
        promptGuess(gameId: gameId)
        return
    }

    // check: all digits must be unique
    let uniqueDigits = Set(input)
    if uniqueDigits.count != 4 {
        print("⚠️ Digits gotta be unique! No repeats.")
        promptGuess(gameId: gameId)
        return
    }

    sendGuess(gameId: gameId, guess: input) { black, white in
        guard let black = black, let white = white else {
            print("❌ No response. Try again.")
            promptGuess(gameId: gameId)
            return
        }

        print("🧠 You got: \(black) black, \(white) white")

        if black == 4 {
            print("🎉 You nailed it! Good job!")
            exit(0)
        } else {
            promptGuess(gameId: gameId)
        }
    }
}

// kicks off the game
func runGame() {
    startGame { gameId in
        guard let gameId = gameId else {
            print("❌ Couldn't start the game :(")
            exit(1)
        }

        print("🎯 Game started! A secret 4-digit code was made.")
        print("Digits are from 1 to 6, like 1234 or 5566")
        print("Type 'exit' to give up lol")

        promptGuess(gameId: gameId)
    }
}

// actually start everything
runGame()
RunLoop.main.run()