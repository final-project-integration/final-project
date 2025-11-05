PFM Project

# Overview

Hamilton Heights Software, LLC will develop a retail Personal Finance Manager (PFM) Java application that can run on a MAC or Windows PC. The PFM will be multi-user, allowing the creation of different user accounts, for the purpose of analyzing a user's annual income and expenses. The twenty-nine CSC 221 employees are divided into seven teams. Each team will be responsible for:

- Selecting a team lead. The team lead's job is to delegate assignments to the other team members, and will be the primary communication contact with the PO.
- Specifying the contract for their module.
- Timely delivery of their module in various stages.
- Attending semi-weekly standups and selecting a spokesperson for the team.
- User input and output as required for their module's menus

# DEV teams

| **Team Name** | **Responsibilities** |
| --- | --- |
| Accounts | CRUD\* user account maintenance, authentication |
| Integration | Main menu; JAR assembly; resolve disputes; PO communications |
| Prediction | What if scenarios to balance a budget |
| Reports | Print requested reports to console or file |
| Storage | CRUD\* user data; maintain budget object |
| Test | Product QA, including creating test scripts and bug tracking |
| Validation | Validate user entries and data meet pre-conditions |

\* CRUD stands for Create, Read, Update, Delete.

# Code requirements

- Use camelCase for method and variable identifiers.
- Use CapitalCase for class, enum, record, and interface identifiers.
- Use SCREAMING_SNAKE_CASE for public static field constants.
- All classes, interfaces, methods and constructors must be Javadoc-ready.
- Primary author must be identified in each Javadoc comment. For classes, identify all authors in member methods and constructors.
- Each bug fix must be documented in Javadoc with @bug, including number and synopsis.
- All code must be correct, robust and efficient.
- An Open or Reopened bug must be Fixed or Closed within 7 days.

# Special notes for Integration and Test teams

## Integration

- Overall responsibility for assembling the product and delivery to the PO on demand.
- Responsible for coding main user menu after authentication. You may wish to develop and share the menu driver code with the other teams for menu display consistency.
- Resolves disputes between teams regarding functionality and structure.
- Communication conduit for inter-team issues with PO.
- Decide which Open bugs should be Closed. PO must approve Closed bugs that were not fixed.

## Test

- Delivery of exported Javadoc to PO on demand. Must include @bug tags.
- Prepare manual test scripts based on module functionality.
- Test edge cases and data validation, using reasonable and absurd test data.
- Maintain the Freedcamp cloud database of bugs.
  - Each employee will be invited to have an account tied to their email.
  - Any employee can report bugs in Freedcamp.
  - A subsequent document will detail how to enter and modify bugs.
- Be prepared to display all bugs grouped by status in a digital Board at Standup
- A Fixed bug must be Reopened or Closed within 3 days.

# Schedule

No homework assignments will be due after November 11.

| **Deadline** | **Item** |
| --- | --- |
| October 23 | Team module assignments |
| October 30 | Module summaries due |
| November 6 | Javadoc for all modules due |
| November 13 | First standup meeting |
| November 20 | Alpha build due |
| December 2 | Beta build, Code review due |
| December 9 | Release Candidate delivery |
| December 11 | Presentation |

# Code Review

Each DEV team (but not the Test team) will perform a complete code review of another team's code by December 2. Which team will code review the other team's code will be determined by the PO in the preceding days.

The reviewing team's TL will distribute the code to the team members and assign who will review what. The TL will submit their team's code comments as a file on Freedcamp, indicating their team and the team being reviewed.

# Functional Summary

- All user interaction will be through the console terminal. No GUI is required.
- A user of the PFM must first create a CSV file with three columns. The first line of the CSV must contain the header: Date,Category,Amount. The columns are:
  - **Date** must be in format MM/DD/YYYY.
  - **Category** is one of a list of specified phrases describing an income or expense.
  - **Amount** is the dollar value (no cents). Income is positive and expenses are negative.
- The file represents income and expenses for only one calendar year. The order of the lines is not important. All dates in the file must be of the same year.
- To use the program, a user must create an account with a user name, password, secret question of their choice, and secret answer. The user name and password are required for authentication. The user has the ability to change their password either by supplying their current password, or by answering the secret question correctly. The user may delete their account, which will also delete any data they previously uploaded. User information is stored between sessions. Therefore, the password needs to be stored between sessions, but must be obfuscated or hashed in some manner. When a password is requested for entry from the console, it is okay not to replace the characters with asterisks, as this is not possible with a console.
- Once authenticated, the user can direct the program to read in their income and expense CSV file from a FQPN or the local folder with name YYYY.csv. The program previews and validates the file, reporting any issues. The user then allows the program to continue to read it in to storage. Once the file is read in, it is stored between sessions. The user can load multiple years-worth of income and expense statements (each in a separate file). If an income and expense statement is loaded for a year that already exists for that user, the user is warned and can either cancel or overwrite the existing year's data. If a file contains invalid data records, the user can choose to have the program ignore the invalid data or cancel the operation and manually fix the CSV file for a later attempt. The user can delete any year's data.
- The user can choose to analyze any year's data. The analysis will summarize the user's transactions per month by determining monthly income minus expenses, and providing totals of each category for the year, along with overall budget performance in a meaningful report.
- The analysis can be printed out to the screen or to a CSV file for further manipulation on a spreadsheet.
- The Prediction part of the program should also be able to perform one or more simple What If calculations at the user's request. For example, for a deficit budget, the user should be able to ask how much to reduce their monthly or annual Entertainment purchases to have a break-even budget. Similarly, for a surplus budget, the user should be able to ask how much more they could spend on Appearance.

# Line Items

## Income

- **Compensation**: Net pay after taxes and other deductions
- **Allowance**: Contributions from others, including financial aid
- **Investments**: Unearned income after taxes
- **Other**: Any other income

## Expenses

- **Home**: Rent, mortgage, repairs, furniture, housewares, insurance, cleaning
- **Utilities**: Gas, electric, phone, ISP for home
- **Food**: Groceries, non-entertainment meals
- **Appearance**: Clothing, grooming
- **Work**: Unreimbursed business expenses, uniforms, work-related services
- **Education**: Tuition, tutors, supplies
- **Transportation**: Taxis, transit fares, personal vehicle (insurance, lease, fuel, repairs)
- **Entertainment**: Subscriptions, dining out, admissions, gifts, vacations
- **Professional Services**: Unreimbursed medical, lawyers, consultants
- **Other**: All other expenses

Do not include one-time capital expenses in the budget.

# Grading

Each member of the team receives the same grade. The final project grade represents 10% of your overall grade.

| **Item** | **Points** |
| --- | --- |
| Timely delivery of your team's materials | 20  |
| Completeness of contracted functionality | 20  |
| Correctness, Robustness, Efficiency | 20  |
| Complete Javadoc, including @bug tags | 10  |
| Code reviewed performed and posted on time | 10  |
| Standup attendance, preparation and delivery | 10  |
| Customer presentation (December 11) | 10  |

# Standup Meetings

- Begins at the beginning of every class meeting, starting Thursday, November 13.
- Team attendance grade loses one point for every team member that misses each start of the standup meeting with an unexcused absence.
  - Excused absences will be granted to a team member if they email the PO before 9 am on the day of the standup meeting, and the excuse does not include the word "traffic", "subway", "train", or "overslept". You know what I mean. Excuses may be subject to verification. If you are ill, you will be excused from the standup attendance, but not from class.
- Select your team's best communicator as the spokesperson (it doesn't have to be the TL and it can change). Any team member should be prepared to speak if the spokesperson is absent, or if you need to answer a question about your code.
- Standup meeting begin at the beginning of class with the Product Owner (me) giving a one-minute summary of project status. Each team's spokesperson then has two minutes to do their presentation.
  - Start the presentation with: "I'm _first-name_ of _team-name_." Other staff may not know who you are. In the first standup meeting, introduce your teammates too.
  - What your team was working on since the last standup meeting
  - What your team will be doing until the next standup meeting
  - What your team is being challenged by, or is blocked doing
- Class instruction resumes after the standup.

# Customer Presentation December 11

- For all teams except Test:
  - You have five minutes for your team to give your presentation.
  - One or more members of your group will demonstrate the part of the module they worked on.
  - You will accept and answer questions, time permitting.
- For the Test team:
  - You have five minutes to give your presentation. One or more members can present.
  - You will display a graph of the burn-down rate of bugs over the course of the project, as well as any bugs that were not fixed.
  - You will demonstrate some examples of edge and robustness testing you did.
  - Summarize the total number of bugs by module.
  - You will display a graph of the burn-down rate of bugs over the course of the project, as well as any bugs that were not fixed.
  - You will demonstrate some examples of edge and robustness testing you did.
  - Summarize the total number of bugs by module.
