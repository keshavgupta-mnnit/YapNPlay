# City Bazaar — Rules Document

**Version:** 0.2.0
**Last updated:** 2026-10-17

City Bazaar is YapNPlay's Indian city-trading board game, based on the
original Indian "Business" game, with its own names and art. These rules are
what the `games/citybazaar` engine encodes. The owner approved them on
2026-10-17.

---

## 1. Players and money

- **Players:** rooms with friends have 3–6; random tables always have 4.
- **Starting balance:** ₹20,000 each, set in AppManager
  (`city_bazaar_starting_cash`). Money is digital only.
- **START:** ₹1,500 every time a player passes or lands on START, including
  when moved by a card.
- **Player colours:** Red, Green, Yellow, Blue, Purple, Orange, given out by
  seat.

## 2. Board

36 tiles in a clockwise ring, with START at the bottom of the screen. The
groups are the original game's: four city colours of 5 cities each,
utilities (purple, 2) and transit (orange, 4).

**Card values:** each city's card is its price, then rent on bare land, with
1, 2 or 3 houses, and with a hotel. One build price per city pays for each
house and for the hotel.
- **Original values:** Chandigarh, Darjeeling, Delhi, Mumbai (land rent,
  1-house rent, build price), Amritsar (price, land rent) and Railways come
  from the original game's cards.
- **Derived values:** Mumbai's 2-house, 3-house and hotel rents follow
  Delhi's pattern. Amritsar's house rents follow Chandigarh's, and its build
  price is assumed.
- **Everything else** is from the spec.

| # | Tile | Group | Price | Rent: land / 1H / 2H / 3H / hotel | House = hotel |
|---|---|---|---|---|---|
| 0 | START | +₹1,500 | | | |
| 1 | Cochin | Red | 3,000 | 400 / 1,200 / 2,400 / 3,600 / 5,000 | 1,500 |
| 2 | Internet | Utility | 6,000 | 800 | |
| 3 | Railways | Transit | 9,500 | 1,000 | |
| 4 | Chandigarh | Red | 2,500 | 200 / 900 / 1,600 / 2,500 / 3,500 | 3,000 |
| 5 | Income Tax | pay ₹1,000 | | | |
| 6 | Indore | Blue | 1,500 | 200 / 600 / 1,200 / 1,800 / 2,500 | 1,000 |
| 7 | Luck | card | | | |
| 8 | Jaipur | Blue | 3,000 | 400 / 1,200 / 2,400 / 3,600 / 5,000 | 1,500 |
| 9 | JAIL | landing = jail | | | |
| 10 | Delhi | Green | 6,000 | 750 / 3,000 / 4,300 / 5,500 / 7,500 | 5,000 |
| 11 | Ahmedabad | Green | 3,500 | 450 / 1,400 / 2,800 / 4,200 / 5,500 | 1,500 |
| 12 | Electricity | Utility | 3,500 | 500 | |
| 13 | Roadways | Transit | 3,500 | 500 | |
| 14 | Amritsar | Yellow | 3,300 | 300 / 1,350 / 2,400 / 3,750 / 5,250 | 3,000 |
| 15 | Goa | Yellow | 4,000 | 500 / 2,000 / 3,500 / 5,000 / 6,500 | 1,500 |
| 16 | Shagun Chest | card | | | |
| 17 | Hyderabad | Yellow | 3,500 | 450 / 1,400 / 2,800 / 4,200 / 5,500 | 1,500 |
| 18 | CLUB | pay ₹500 | | | |
| 19 | Agra | Blue | 2,500 | 350 / 1,000 / 2,000 / 3,000 / 4,000 | 1,000 |
| 20 | Luck | card | | | |
| 21 | Kanpur | Blue | 4,000 | 500 / 1,500 / 3,000 / 4,500 / 6,000 | 2,000 |
| 22 | Patna | Blue | 2,000 | 250 / 800 / 1,600 / 2,400 / 3,200 | 1,000 |
| 23 | Darjeeling | Red | 2,500 | 200 / 1,200 / 2,600 / 3,500 / 5,000 | 3,000 |
| 24 | Airways | Transit | 11,000 | 1,500 | |
| 25 | Ladakh | Red | 6,500 | 850 / 2,400 / 4,800 / 7,200 / 9,500 | 3,000 |
| 26 | Shimla | Red | 3,500 | 450 / 1,400 / 2,800 / 4,200 / 5,500 | 1,500 |
| 27 | REST HOUSE | skip next turn | | | |
| 28 | Chennai | Yellow | 7,000 | 900 / 2,600 / 5,200 / 7,800 / 10,000 | 3,500 |
| 29 | Shagun Chest | card | | | |
| 30 | Bengaluru | Yellow | 5,000 | 650 / 1,900 / 3,800 / 5,700 / 7,500 | 2,500 |
| 31 | Wealth Tax | ₹50 per building | | | |
| 32 | Pune | Green | 3,000 | 400 / 1,200 / 2,400 / 3,600 / 5,000 | 1,500 |
| 33 | Kolkata | Green | 4,000 | 500 / 1,500 / 3,000 / 4,500 / 6,000 | 2,000 |
| 34 | Waterways | Transit | 3,000 | 400 | |
| 35 | Mumbai | Green | 8,500 | 1,200 / 4,000 / 5,700 / 7,300 / 10,000 | 7,000 |

## 3. A turn

1. Roll the two dice and move clockwise by the total. Doubles mean nothing,
   except as the way out of jail.
2. Collect ₹1,500 when passing or landing on START.
3. Resolve the tile (§4–§7). Then it's the next player's turn.

Each decision has 20 seconds.

## 4. Unowned city, transit or utility

The player buys it or declines. Declining leaves it unowned; there's no
auction. A player who can't afford it gets no offer, only a "not enough
balance" note, and the turn moves on.

## 5. Rent and building

- **Rent:** the tier's rent: bare land, 1–3 houses or hotel for cities, the
  fixed rent for transit and utilities.
- **3 of a colour:** an owner with 3 or more tiles of a colour gets **double
  rent** on that colour, at whatever tier.
  **Example:** Patna earns 250 on bare land and 1,600 with 2 houses. With 3
  blue cities, those become 500 and 3,200.
- **Building:** landing on your own city offers **one** house, up to 3
  houses and then a hotel (the most a city can have). A full colour group
  is not needed.

## 6. Jail

- **Getting in:** landing on JAIL, or a "Go to Jail" card (one in each
  deck).
- **Each turn in jail:** up to 3 rolls for doubles. Doubles free the player,
  who moves on their next turn.
- **After 3 failed rolls,** the player chooses:
  - **Pay ₹500:** free; they move on their next turn.
  - **Half rent:** they stay in jail, and every rent they collect is halved
    until their next turn.
- The same repeats every turn until they're free.

## 7. Other tiles

- **Taxes:**
  - **Income Tax:** ₹1,000.
  - **Wealth Tax:** ₹50 for every building the player owns (each house and
    each hotel counts as one).
  - **CLUB:** ₹500.

  All are paid to the bank.
- **REST HOUSE:** skip the next turn.
- **Luck (?) and Shagun Chest (₹):** there's no new roll. The dice total
  that brought the player onto the tile picks the result. Tapping a pile,
  or a Luck or Shagun Chest tile, shows its table with the amounts.

  The original game prints these tables on its board, and they aren't
  available online, so the results below are ours. **Review them.**

| Roll | Luck (?) | Shagun Chest (₹) |
|---|---|---|
| 2 | Lottery win +₹2,500 | Wedding shagun +₹1,500 |
| 3 | Go to Jail | Medical bill −₹1,000 |
| 4 | Traffic fine −₹1,000 | Income tax refund +₹2,000 |
| 5 | Advance to START | Go to Jail |
| 6 | Scholarship +₹1,000 | Diwali bonus +₹1,000 |
| 7 | Go back 3 tiles | Insurance premium −₹1,500 |
| 8 | Crossword prize +₹1,000 | Bank interest +₹500 |
| 9 | Advance to the Club | Advance to START |
| 10 | Advance to Mumbai | School fees −₹750 |
| 11 | Birthday: ₹500 from each player | Festival gifts: ₹250 from each player |
| 12 | Treat everyone: ₹250 to each player | Property tax: ₹50 per property owned |

## 8. Short of money

1. **Buildings first, in reverse:** a hotel goes back to 3 houses, then one
   house at a time. Each sale pays half the build price.
2. **Bare cities** can then be sold to the bank for half the price, or put
   up for **auction**.
3. **Auction rules:**
   - **Who bids:** every other player gets a prompt. Spectators can't bid.
   - **The prompt shows:**
     - The city's card.
     - Who owns the rest of its colour, with a warning when the buyer would
       reach 3 of a colour.
     - The current bid and the step.
     - Your balance after winning.
   - **Bidding:** it opens at the bank value (half the price). Steps grow
     with the price: +₹50 under ₹1,000, +₹100 under ₹3,000, +₹250 under
     ₹6,000, then +₹500.
   - **Timer:** 5 seconds, restarting after every bid.
   - **Ending:** the highest bidder pays the seller and takes the city, with
     no backing out. If nobody bids, the bank buys it at half price. The
     money goes to the seller, who then pays the debt.
4. **Bankruptcy:** if everything sold still doesn't cover the debt, the
   player is bankrupt. Their cities go back to the bank, whatever cash is
   left goes to the creditor, and they are out.

## 9. Timeouts, Autoplay and the bot

- **Turn timer runs out:** the bot plays the turn for the player. It rolls
  and moves, never buys or builds, stays in jail on half rent, and sells
  only what a debt needs, least-earning first. If the player didn't roll
  and Autoplay is off, ₹50 is deducted.
- **Autoplay on (or the app in the background):** the same, with no fine.
- **CPU players:**
  - Buy and build when they keep a reserve.
  - Pay their way out of jail when they can spare it.
  - Bid in auctions up to the price, or 1.5× the price if it makes 3 of a
    colour.

## 10. End of the match

- The last player not bankrupt wins.
- **Time limit:** 30 minutes (AppManager `city_bazaar_time_limit_minutes`),
  with an on/off room toggle.
- **Round limit:** 30 rounds (AppManager `city_bazaar_round_limit`), with an
  on/off room toggle.
- When either runs out, everyone is ranked by **net worth**: balance + city
  prices + build prices of buildings.

## 11. Rooms and seats

Leaving, idle seats and spectators will work the same way as in Royal Ludo
(the shared seat handover). This is **not built yet**: today a player who
leaves is replaced by the bot. Spectators exist only in room matches.
