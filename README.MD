# Error Codes

Other status codes are proxied from hypixel.

| Code | Message                                               | Reason                                                                                                                                          |
|------|-------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| 404  | Endpoint not implemented                              | Some default Hypixel endpoints are disabled to prevent abuse                                                                                    |
| 405  | Only GET method is allowed                            | Methods such as POST, PUT, DELETE are disabled                                                                                                  |
| 400  | Parameter(s) required for this endpoint               | Parameter such as `profile`, `uuid`, etc.                                                                                                       |
| 414  | Parameter(s) value is too long                        | If the parameter exceeds 36 character. This usually means the value is malformed. The longest existing Parameter is UUID which is 36 character. |
| 500  | Internal Error encountered. Admins have been notified | An exception appeared that has not been expected                                                                                                |

# Endpoints

- Endpoints such as `/guild` are disabled by default due to it being mostly used by backends bots
- Endpoints such as `/status` and `/skyblock/auction` are disabled by default due to its heavy usage in automated
  tracking
  bots

| Endpoint             | Parameters      | Cache      | Cache Reason                                                                                           |
|----------------------|-----------------|------------|--------------------------------------------------------------------------------------------------------|
| `/player`            | `uuid`          | 12 Hours   | Endpoint mostly used to check overall stats/rank                                                       |
| `/recentgames`       | `uuid`          | 60 Minutes | Endpoint used to view an overview of a player. Stalking isn't required                                 |
| `/skyblock/profiles` | `uuid`          | 5 Minutes  | Used completively for setup checking when partying                                                     |
| `/skyblock/profile`  | `profile`'s id  | 30 Minutes | Longer than `profiles`. Due to its potential abuse with auctions api                                   |
| `/skyblock/museum`   | `profile`'s id  | 12 Hours   | Endpoint used to have an overview of stats                                                             |
| `/skyblock/bingo`    | `uuid`          | 2 Hours    | Can be used to track each other progress. Too fast update will result in abuse with automated trackers |

# Headers

- Extra headers are returned to indicate server health
- No extra headers required to fetch data

| Header      | Explanation                                                                                                                                              |
|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `X-age`     | How old the returned data. Timestamp in milliseconds. Useful to indicate the cache age. Current timestamp will be returned if the data is newly fetched. |
| `X-name`    | Server name.                                                                                                                                             |
| `X-version` | Server version.                                                                                                                                          |