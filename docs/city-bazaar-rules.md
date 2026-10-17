# City Bazaar — Rules Document

**Version:** 0.3.0
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

36 tiles in a clockwise ring, laid out like the original "Business" board:
START bottom-left, JAIL top-left, CLUB top-right, REST HOUSE bottom-right.
There are four city colours (red, blue, green, yellow) of 5 cities each,
and 6 grey **company** cards that work in pairs.

**Card values:** a city's card gives its price, then rent on bare land,
with 1, 2 or 3 houses, and with a hotel. One build price per city pays for
each house and for the hotel. A company's card gives its rent alone, and
its rent when the same player also owns its pair.

- **Original values:**
  - Chandigarh, Darjeeling, Delhi, Srinagar: everything.
  - Mumbai: land rent, 1-house rent, build price.
  - Amritsar: price and land rent.
  - Railways and B.E.S.T.: both rents.
  - Water Works: both rents.
  - Every price on the board.
- **Derived values (review):** all other rents and build prices, and the
  Air India, Electric Company and Motorboat rents. They follow cities and
  companies of similar price.

| # | Tile | Group | Price | Rent: land / 1H / 2H / 3H / hotel | House = hotel |
|---|---|---|---|---|---|
| 0 | START | +₹1,500 | | | |
| 1 | Mumbai | Red | 8,500 | 1,200 / 4,000 / 5,700 / 7,300 / 10,000 | 7,000 |
| 2 | Water Works | Company | 3,200 | 500; 1,000 with Air India | |
| 3 | Railways | Company | 9,500 | 1,000; 1,350 with B.E.S.T. | |
| 4 | Ahmedabad | Red | 4,000 | 500 / 1,500 / 3,000 / 4,500 / 6,000 | 2,000 |
| 5 | Income Tax | ₹50 per property owned | | | |
| 6 | Indore | Blue | 1,500 | 200 / 600 / 1,200 / 1,800 / 2,500 | 1,000 |
| 7 | Luck | card | | | |
| 8 | Jaipur | Blue | 3,000 | 400 / 1,200 / 2,400 / 3,600 / 5,000 | 1,500 |
| 9 | JAIL | landing = jail | | | |
| 10 | Delhi | Green | 6,000 | 750 / 3,000 / 4,300 / 5,500 / 7,500 | 5,000 |
| 11 | Chandigarh | Green | 2,500 | 200 / 900 / 1,600 / 2,500 / 3,500 | 3,000 |
| 12 | Electric Company | Company | 2,500 | 400; 800 with Motorboat | |
| 13 | B.E.S.T. | Company | 3,500 | 600; 1,100 with Railways | |
| 14 | Simla | Yellow | 2,200 | 250 / 900 / 1,800 / 2,700 / 3,600 | 1,000 |
| 15 | Amritsar | Yellow | 3,300 | 300 / 1,350 / 2,400 / 3,750 / 5,250 | 3,000 |
| 16 | Shagun Chest | card | | | |
| 17 | Srinagar | Yellow | 5,000 | 550 / 3,500 / 5,000 / 7,000 / 8,000 | 6,000 |
| 18 | CLUB | pay ₹500 | | | |
| 19 | Agra | Blue | 2,500 | 350 / 1,000 / 2,000 / 3,000 / 4,000 | 1,000 |
| 20 | Luck | card | | | |
| 21 | Kanpur | Blue | 4,000 | 500 / 1,500 / 3,000 / 4,500 / 6,000 | 2,000 |
| 22 | Patna | Blue | 2,000 | 250 / 800 / 1,600 / 2,400 / 3,200 | 1,000 |
| 23 | Darjeeling | Red | 2,500 | 200 / 1,200 / 2,600 / 3,500 / 5,000 | 3,000 |
| 24 | Air India | Company | 10,500 | 1,200; 2,000 with Water Works | |
| 25 | Calcutta | Red | 6,500 | 850 / 2,400 / 4,800 / 7,200 / 9,500 | 3,000 |
| 26 | Hyderabad | Red | 3,500 | 450 / 1,400 / 2,800 / 4,200 / 5,500 | 1,500 |
| 27 | REST HOUSE | skip next turn | | | |
| 28 | Chennai | Yellow | 7,000 | 900 / 2,600 / 5,200 / 7,800 / 10,000 | 3,500 |
| 29 | Shagun Chest | card | | | |
| 30 | Bengaluru | Yellow | 4,000 | 500 / 1,500 / 3,000 / 4,500 / 6,000 | 2,000 |
| 31 | Wealth Tax | ₹50 per building owned | | | |
| 32 | Ootacamund | Green | 2,500 | 350 / 1,000 / 2,000 / 3,000 / 4,000 | 1,000 |
| 33 | Cochin | Green | 3,000 | 400 / 1,200 / 2,400 / 3,600 / 5,000 | 1,500 |
| 34 | Motorboat | Company | 3,000 | 450; 900 with Electric Company | |
| 35 | Goa | Green | 4,000 | 500 / 2,000 / 3,500 / 5,000 / 6,500 | 1,500 |

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

- **Rent:** the tier's rent: bare land, 1–3 houses or hotel for cities.
  A company earns its own rent, or its pair rent when the same player
  owns both companies of the pair (Water Works + Air India, Railways +
  B.E.S.T., Electric Company + Motorboat).
- **3 of a colour:** an owner with 3 or more cities of a colour gets
  **double rent** on that colour, at whatever tier. Companies aren't a
  colour.
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
  - **Income Tax:** ₹50 for every property the player owns.
  - **Wealth Tax:** ₹50 for every building the player owns (each house and
    each hotel counts as one).
  - **CLUB:** ₹500.

  All are paid to the bank.
- **REST HOUSE:** skip the next turn.
- **Luck (?) and Shagun Chest (₹):** there's no new roll. The dice total
  that brought the player onto the tile picks the result. Tapping a pile,
  or a Luck or Shagun Chest tile, shows its table with the amounts.

  - **Luck:** odd totals pay the player, even totals cost them.
  - **Shagun Chest:** the other way round: even totals pay, odd totals
    cost.

  This odd/even split is the original game's. Its full tables aren't
  available online, so the entries below follow the examples printed on
  the board. **Review them.**

| Roll | Luck (?) | Shagun Chest (₹) |
|---|---|---|
| 2 | Loss in share market −₹2,000 | Your birthday: ₹500 from each player |
| 3 | Lottery prize +₹2,500 | Go to Jail |
| 4 | Fine for reckless driving −₹1,000 | Beauty contest winner +₹2,500 |
| 5 | Advance to START | School fees −₹1,500 |
| 6 | House repairs −₹1,500 | Income tax refund +₹2,000 |
| 7 | Crossword prize +₹1,000 | Insurance premium −₹1,500 |
| 8 | Go to Jail | Sold old scrap +₹1,000 |
| 9 | Advance to Mumbai | Medical bill −₹1,000 |
| 10 | Treat everyone: ₹250 to each player | Profit in share market +₹3,000 |
| 11 | Best employee of the year +₹2,500 | Property tax: ₹50 per property owned |
| 12 | Go to Rest House (skip a turn) | Wedding shagun +₹1,500 |

In the app's table, paying rows are filled green and costing rows red.

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
