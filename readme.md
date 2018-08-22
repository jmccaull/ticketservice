#Ticket Service Coding Challenge

I took the project to mean that implementing the TicketService interface as provided was required.
The only changes made were to the documentation to clarify runtime behavior. 
I also assumed that the consumer of the API would either use the implementations of Venue and SuccessfulHoldRepository
I provide, but they are free to implement their own. 

##Building and Testing

The project can be built and tests run with the Gradle wrapper from the root project directory. For convenience the both a jar containing the class files and one with the source files are produced
directly in the root folder of the project (not in the build\libs folder). 

on Windows:
```powershell
gradlew clean
gradlew build
``
Unix:
```bash
./gradlew build
./gradlew clean
```

## Improvements

Depending on the situation and use case, if possible I would change the TicketService API. Having methods that must be called in a specific order without type or object structure enforcing it is a code smell.
Easy fixes would be to make reserveSeats() take in the acutal SeatHold object instead of the id and email or move the confirm functionality into the SeatHold object, having reserveSeats instead be a direct route to reseving (no hold).
Either of these changes would make the intended program flow much less ambiguoius.
