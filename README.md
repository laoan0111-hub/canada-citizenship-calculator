# Canada Citizenship Calculator (GUI)

A simple Java Swing GUI tool to estimate **Canadian citizenship physical presence credit**
using the **rolling 5-year window** rule and **1095 credited days** requirement.

## Features
- GUI (Java Swing)
- Add multiple travel stays (Entry / Exit)
- Supports **ongoing stay** (blank exit = still in Canada)
- Calculates:
  - Credited days as of today (rolling 5-year window)
  - Remaining credited days to reach 1095
  - Earliest eligibility date (qualifying date)
  - Countdown (days) from today to eligibility date
  - The 5-year window used for the qualifying date

## Rules Implemented (Simplified)
- Rolling **5-year window** ending on the date being evaluated.
- Requires **1095 credited days**.
- **Pre-PR** days: credited at **0.5** up to **365 physical days** maximum.
- **Post-PR** days: credited at **1.0**.

> Note: This is an estimation tool for learning and planning. Always verify with official IRCC guidance.

## Requirements
- Java 17+ (or Java 11+ should work if you're not using newer APIs)

Check:
```bash
java -version

