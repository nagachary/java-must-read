### Design Principles

General Software Design Principles

1. KISS - Keep it simple and stupid
2. DRY - Don’t repeat yourself
3. YAGNI - You Aren’t Going to Need it
4. Separation of Concerns
5. Law of Demeter

#### KISS: Keep it simple and stupid
1. The time to add complexity is when simplicity stops working. If your single class grows to 500 lines with ten different responsibilities, that's when you refactor. If adding a new payment method means modifying code in five places, that's when you introduce a strategy pattern. But start simple.

#### DRY - Don’t repeat yourself

1. When you find yourself writing the same logic in multiple places, pull it into one place. If three classes all validate email addresses the same way, create a shared validation method. If two services both need to convert timestamps, put that conversion in a utility function.


2. But don't take DRY too far. If two pieces of code look similar but serve different purposes, sometimes duplication is fine. Forcing them to share code can create artificial coupling where changes to one break the other. The key is whether the logic is conceptually the same, not just textually similar.


#### YAGNI - You Aren’t Going to Need it

1.  Build what you need now, not what you might need later. In interviews, when you're designing a parking lot system, don't add support for valet parking and electric vehicle charging stations unless the requirements specifically mention them. Don't make your classes extensible in every direction just in case.


2. The problem with building for future requirements is you usually guess wrong. You add complexity for scenarios that never happen, and when the actual new requirement comes, it's different from what you prepared for. Now you're stuck maintaining dead code.

#### Separation of Concerns:

1. Different parts of your code should handle different responsibilities, and they shouldn't know about each other's internals. Your UI layer shouldn't contain business logic. Your business logic shouldn't know how data is stored. Your data access layer shouldn't format strings for display.

#### Law of Demeter (Principle of the least knowledge)

1. A method should only talk to its immediate friends, not reach through objects to access distant parts of the system. If you see code like order.getCustomer().getAddress().getZipCode(), that's violating the Law of Demeter.


2. The problem with deep chaining is coupling. Your code now knows the internal structure of three different objects. If any of them change how they organize their data, your code breaks. Instead, put a method on Order called getCustomerZipCode() that handles the navigation internally.

General Principles
```
* KISS → Start simple, add complexity only when needed
* DRY → Reduce duplication, simplify maintenance
* YAGNI → Build for today, not hypothetical futures
* Separation of Concerns → Enable independent testing and changes
* Law of Demeter → Reduce coupling, hide internal structure
```

Object-Oriented Design Principles (SOLID):

1. SRP - Single Responsibility Principle
2. OCP - Open/Closed Principle
3. LSP - Liskov Substitution Principle
4. ISP - Interface Segregation Principle
5. DIP - Dependency Inversion Principle

These principles are grouped under the acronym SOLID and apply specifically when you're designing classes and their relationships.

Modern languages favor simpler approaches—composition over class hierarchies, functions over interfaces. Don't break KISS by forcing SOLID patterns where simpler solutions work fine. In interviews, apply these principles when the problem calls for them, but recognize when you're adding complexity for its own sake.

#### SRP - Single Responsibility Principle:

1.  A class should have one reason to change. If a class mixes multiple concerns, split them. This is the foundation of good class design.

#### OCP - Open/Closed Principle:

1.  Classes should be open for extension but closed for modification. You should be able to add new behavior without changing existing code. This usually means using interfaces or abstract classes so you can add new implementations without touching the original code.


2.  Every time you modify existing code, you risk breaking things that already work. If you design with interfaces from the start, adding new functionality becomes a matter of writing new classes that implement those interfaces. The old code never changes, so it can't break.

#### LSP - Liskov Substitution Principle:

1. Subclasses must work wherever the base class works. If you have a method that accepts a Bird, passing in a Penguin shouldn't break things even though penguins can't fly. This means your subclasses can't violate the expectations set by the parent class.


2. If your code uses a parent class or interface, it should be able to use any subclass without knowing which specific subclass it is. The subclass can add new behavior, but it can't remove or break behavior that the parent promised. When a subclass throws an exception for a method the parent class provides, that's a red flag you're violating LSP.


3. If a subclass forces callers to add special-case logic (e.g., if (bird instanceof Penguin)), you violated LSP.

#### ISP - Interface Segregation Principle:

1.  Prefer small, focused interfaces over large, general-purpose ones. Don't force classes to implement methods they don't need. If a class only needs two methods from an interface with ten methods, that interface is too big.


2.  The problem with fat interfaces is that classes are forced to implement methods they'll never use. This leads to empty implementations or methods that throw exceptions, which is a code smell. Split large interfaces into smaller, cohesive ones.


3.  Classes can implement multiple small interfaces if they need to, but they're not stuck implementing irrelevant methods.

#### Dependency Inversion Principle:

1.  High-level modules shouldn't depend on low-level modules. Both should depend on abstractions. This means your business logic shouldn't directly instantiate concrete classes - it should depend on interfaces.


2.  E.g: Instead of your NotificationService creating a new EmailSender directly, it should receive a MessageSender interface through its constructor. This makes your code more flexible and testable. You can swap out email for SMS without changing NotificationService at all.


SOLID Principles
```
* SRP → Keep classes focused on one responsibility
* OCP → Support future requirements without modifying existing code
* LSP → Prevent brittle hierarchies that break at runtime
* ISP → Keep interfaces clean and focused
* DIP → Keep code flexible and testable through abstraction
```