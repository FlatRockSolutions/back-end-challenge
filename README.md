### Technical challenge

Assume we have a big legacy system and one of the parts is withdrawal processing (the process that allows transfer of money from company to employee accounts). We can now completely rewrite the system, including API change (endpoints, DTOs, etc). We are only interested to see withdrawal processing. You can implement any solution you want. The following acceptance criteria applies:

- Use any architecture you are comfortable with 
- Use modern Java or Kotlin (we use Kotlin for new code)
- Use Spring boot
- Use any database; SQL/NoSQL (please use embedded)
- The code must be tested. We don't expect 100% coverage for this challenge, we want to see that you can write sensible tests. For example, if you have several similar converters, you don't have to test every single class/method, just enough to test one. For critical logic like the withdrawal process we'd like to see coverage of different scenarios.
- We expect to see SOLID principles in action
- The service should be easy to run (e.g. with docker-compose)

#### Here are some business rules of the withdrawal processing:

- We have a list of users (`/find-all-users` endpoint)
- A user has several payment methods
- A user can execute a withdrawal request using one of his payment methods
- A withdrawal can be executed (sent to a payment provider) as soon as possible or scheduled to be executed later
- After the service receives a request it stores a withdrawal object in our DB and sends a transaction request to a payment provider async. Note: for this task we don't care about a transaction completion  
- We noticed that in current solution we are losing some outgoing events w.r.t withdrawals. We MUST 100% notify listeners regarding any withdrawal statuses. That means a new solution should be designed to cover the requirement. For example, when a withdrawal has been sent to a provider, we update a status to processing in the database, and then send a notification. What if the notification failed to send (e.g. connection issues to a messaging provider)?  

#### Steps to proceed:

- Fork the repository if you want to refactor the existing solution, or you can create the project from scratch
- Implement your solution
- If you need to add comments/description regarding the solution please put them in `SOLUTION.md`  
- Once complete invite `oosthuizenr` for review
