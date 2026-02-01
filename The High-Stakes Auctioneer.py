class Bid:
    def __init__(self, user, amount):
        self.user = user
        self.amount = amount

    def __repr__(self):
        return f"{self.user} ({self.amount})"


class Auctioneer:
    def __init__(self):
        self.stack = []

    def bid(self, user, amount):
        if not self.stack or amount > self.stack[-1].amount:
            self.stack.append(Bid(user, amount))
            print(f"Current: {user} ({amount})")
            self.print_stack()
        else:
            print("Error: Too low.")

    def withdraw(self):
        if not self.stack:
            print("Error: No bids to withdraw.")
            return

        removed = self.stack.pop()
        print(f"{removed.user} retracted.")

        if self.stack:
            top = self.stack[-1]
            print(f"Reverted to {top.user} ({top.amount}).")
        else:
            print("No active bids.")

        self.print_stack()

    def current(self):
        if not self.stack:
            print("No active bids.")
        else:
            top = self.stack[-1]
            print(f"Current: {top.user} ({top.amount})")

    def print_stack(self):
        amounts = [bid.amount for bid in self.stack]
        print(f"Stack: {amounts}")


# ---- CLI Simulation ----
auction = Auctioneer()

while True:
    try:
        command = input("> ").strip().split()

        if not command:
            continue

        if command[0] == "BID":
            user = command[1]
            amount = int(command[2])
            auction.bid(user, amount)

        elif command[0] == "WITHDRAW":
            auction.withdraw()

        elif command[0] == "CURRENT":
            auction.current()

        elif command[0] == "EXIT":
            break

        else:
            print("Unknown command.")

    except Exception as e:
        print("Invalid command format.")
