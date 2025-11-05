
## Personal Finance Manager-Final Project

This project is a command-line Personal Finance Manager built by multiple teams working together.
Each module handles a different part of the system (Accounts, Storage, Reports, etc.).

The **Integration Team** builds the main menu, connects all modules, and ensures everything runs smoothly.


###  Features

* User login & account system
* Load/save budget data (CSV)
* Validate user inputs & CSV files
* View financial reports
* Predict future budget outcomes
* Central menu + module routing (Integration team)


###  Repo Structure

```
integration/   → Main menu + routing (our team)
accounts/      → Login & authentication
storage/       → File I/O (load/save CSV)
validation/    → Check user input + file format
reports/       → Monthly/yearly summaries
prediction/    → Budget forecasting
docs/          → Project docs + planning
```

### Program Flow

```
Login → Main Menu → Choose a feature → Module runs → Return to menu


###  Dev Workflow

* Work on your module folder
* Create a branch → make changes → PR → approval → merge
* CodeOwner review & approval
* No direct commits to `main`

## Pull Request Rules

- Fill the PR template completely (no blank sections).
- Tag the Integration lead if modifying any shared interfaces.
- Verify the project runs locally before opening the PR.
- Get at least one teammate review before merge.
- If your change affects module interfaces or public methods, add the `api-change` label and notify Integration.


###  Running the Program

> Instructions will be added after modules connect.

###  Status

 Repo + branches set up
 Integration menu starting
 Start connecting modules

###  Notes

* Use clear commits & PRs
* Communicate between teams

## Key Milestones
 Oct 30 --- Functional Summary due 
 Nov 6  --- Javadoc complete 
 Nov 20 --- Alpha build ready 
 Dec 2  --- Beta + code review 
 Dec 9  --- Release candidate 
 Dec 11 --- Final demo 


