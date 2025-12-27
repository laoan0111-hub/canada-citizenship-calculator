```markdown
# Canada Citizenship Calculator (1095 days, rolling 5-year window)

A **Java desktop GUI** tool to calculate Canadian citizenship eligibility based on
the **rolling 5-year window** and **1095 credited days** rule, including
**pre-PR half-credit (max 365 physical days)** and **post-PR full credit**.

---

## Features

- GUI (Java Swing)
- Multiple **entry / exit** records supported
- **Blank exit = ongoing stay**
- Correct **rolling 5-year window** logic
- Pre-PR: **0.5 credit per day**, capped at **365 physical days**
- Post-PR: **1.0 credit per day**
- Calculates:
  - Credited days **as of today**
  - Remaining days to **1095**
  - **Earliest qualifying (eligibility) date**
  - Countdown (days)
  - Window actually checked

---

## Screenshot

![GUI Demo](screenshots/GUI%20demo.png)

## Design Notes

- The GUI layer does not perform any date or credit calculation.
- All business logic is encapsulated in `CitizenshipCalculator`.
- `CreditResult` is a value object used to expose pre-PR and post-PR credit breakdown.
- This separation allows the calculator to be reused in CLI, GUI, or web applications.


## Files

- `Main.java`  
  GUI entry point (Swing UI, user interaction)

- `CitizenshipCalculator.java`  
  Core calculation logic (no UI)

- `Stay.java`  
  Data model for one stay (entry / exit)

---

## How the calculation works (summary)

1. For a given date **D**, define a window:  
   `window = [D − 5 years, D]`

2. Count only days **inside the window**

3. Split days:
   - **Before PR date** → 0.5 credit/day, max **365 physical days**
   - **On / after PR date** → 1.0 credit/day

4. Find the **earliest date D** such that:
```

credited_days(D) ≥ 1095

````

---

## How to run

```bash
cd day2-citizenship-app
javac *.java
java Main
````

---

## Input rules (GUI)

* Dates must be `YYYY-MM-DD`
* Add multiple stays using **Add stay**
* Leave **Exit blank** if the stay is ongoing
* Click **Calculate** to compute results

---

## Example

PR date:

```
2025-10-08
```

Stays:

```
2022-09-21 → ongoing
```

Result:

```
Eligibility date (qualifying date): 2027-10-09
Countdown (days): XXX
```

---

## Notes

* This tool is for **planning / estimation only**
* Always verify with IRCC official calculator before applying

---

## License

MIT (or personal use)

```
```
