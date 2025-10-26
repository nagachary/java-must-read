# Java Memory Management

It is essential to understand the memory structure and management in java virtual machine for developing high performing java applications efficiently.

JVM is divided into several logical data areas each perform specific role during the program execution.

These logical areas in memory are

* Heap Memory
  * Young Generation
    * Eden Space
    * Survivor Space (S0)
    * Survivor Space (S1)
  * Old Generation
* Stack Memory
* Method Area
  * PermGen (Before Java 8)
  * MetaSpace (Java 8 and Later)
* Program Counter (PC)
* Native Method Stack

Understanding memory areas is crucial in fine-tuning the program for better performance.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
## Heap Memory

Heap is a shared runtime data area where objects and arrays are shared. There exists only one Heap for a running JVM process. Heap memory is created when JVM starts.
Heap Memory stores all the objects created during the program execution. The heap is a shared memory space, accessible by all threads in the JVM. 
This shared access allows objects to be passed between threads and persist beyond the execution of any single method. Heap memory is managed by garbage collector which reclaims object memory which is no longer in use.

The JVM divides heap into two regions: The Young Generation and the Old Generation. This layout, known as the Generational Heap Model, is based on the principle that most objects in Java applications are short-lived, and those that survive are likely to live much longer.

### Young Generation

All newly created objects start their lifecycle in young generation.It is optimized for fast allocation and frequent garbage collection.

    * Method local objects
    * Temporary buffers
       
Young generation is divided into three types: 

    * Eden Space
    * Survivor Space (S0)
    * Survivor Space (S1)

##### Eden Space: 
* This is the starting point of the new object allocation. Once the object is created, the JVM attempts place it in Eden. When Eden is full, a minor garbage collector is triggered. 

##### Survivor Space (S0 & S1): 
* The two survivor spaces, commonly referred to as S0 and S1, which act as staging areas for objects that survive a garbage collection. 
* After each Minor GC, reachable objects from Eden are moved into one of the survivor spaces. Objects that continue to survive are moved between the two survivor spaces across collection cycles.

Proper tuning of the Young Generation can help reduce promotion rates (to old generation) and delay costly collections in the Old Generation. 

For example, you can adjust:
    
* ```The size of the Young Generation with -Xmn```
* ```The Eden-to-Survivor space ratio using -XX:SurvivorRatio```
* ```The promotion age threshold with -XX:MaxTenuringThreshold```

### Old Generation:

As objects survive more garbage collection cycles, their age increases. Once an object’s age exceeds a threshold (controlled by the JVM flag ```-XX:MaxTenuringThreshold```), it is promoted to the **Old Generation**.
The Old Generation, also known as the Tenured Generation, is designed to hold long-lived objects, those which have survived the multiple Minor Garbage Collections (GCs).
The Old generation objects are collected less frequently as the objects in Old generation are long-lived. 

    * Persistent application-level caches
    * Large object graphs such as sessions or user data
    * Static or shared data structures that are retained across requests

To control the size and behavior of the Old Generation, you can adjust:

* ```The total heap size using -Xmx (maximum) and -Xms (initial)```
* ```The size of the Young Generation using -Xmn, which affects how much memory is left for the Old Generation```
* ```The ratio between the two using -XX:NewRatio```

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
## Stack Memory

Unlike heap memory, Stack memory is not shared among threads. Stack is thread-local and exists independently for each thread. 
When a thread is created , JVM allocates a new Java stack for that thread. The JVM stack is used to store method execution data, method calls, including local variables, references to objects method arguments and
return addresses.

Every time a method is invoked, the JVM allocates a new **stack frame** on the thread’s stack. This frame is a self-contained unit of memory that holds all the necessary data for executing that method.
Stack frames are lightweight and quick to allocate. Since the stack frames are thread-local and do not require synchronization, operations on the stack (method calls and returns) are extremely fast.
Stack memory ideal for handling short-lived, method-scoped data.

    * Primitive values (e.g., int, double, boolean)
    * Object references: pointers to objects that live in the heap
    * Method parameters: values passed in when the method is called
    * Intermediate results
    * Operand stack

Once a method completes execution, its associated stack frame is removed automatically.
Each thread’s stack is limited in size, which can be configured using the ```-Xss``` JVM option.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
## Method Area

Method area is a logical part of the Heap memory and is created when the JVM starts.
Method area is used to store class-level information such as class structures, Method bytecode, Static variables, Constant pool, Interfaces.
Static variables in Java are stored in the Method Area.
This area is logically defined by the JVM Specification and is shared among all threads.
When a class or interface is loaded by the JVM, its definition is parsed 

    * Class structure metadata, including class names, superclasses, implemented interfaces, and modifiers (public, final, etc.)
    * Runtime constant pool, which contains literal values and symbolic references used by the class
    * Static variables, which are class-level variables shared across all instances
    * Field and method information, including method signatures, access modifiers, and bytecode instructions
    * Constructor code, including initialization routines for object creation
    * Type information used for method resolution and dispatching

### PermGen (Before Java 8):

Prior to Java 8, method area was physically implemented in a fixed-size memory region called the Permanent Generation (PermGen). 
PermGen resided in the heap and had to be explicitly sized using JVM flags :

```
-XX:PermSize=128m
-XX:MaxPermSize=256m
```

### MetaSpace (Java 8 and Later):

From Java 8 onwards, PermGen has been replaced with MetaSpace, Unlike PermGen, Metaspace is not part of the Java heap.
However, while Metaspace can grow automatically, it is still constrained by available system memory. If too many classes are loaded (or not properly unloaded), Metaspace can still overflow, leading to error.

To control Metaspace usage, the JVM provides the following flags

```
-XX:MetaspaceSize=128m
-XX:MaxMetaspaceSize=512m
```
Metaspace memory is eligible for collection when classes are unloaded.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
## Program Counter (PC)

Each JVM thread has a Program Counter (PC) register. It plays a critical role in tracking the flow of execution for Java applications. 
While other memory areas like the heap and stack handle data and objects, the PC register is concerned with instruction-level control: keeping track of which bytecode instruction a thread should execute next.

The Program Counter register contains the address of the next instruction to be executed in the current thread’s method. This allows the JVM to resume execution from the correct point after:

    * A method call
    * A branch (e.g., if/else, loop)
    * An exception handler
    * A thread context switch

Internally, the PC register helps drive the JVM execution engine, which fetches the bytecode instruction pointed to by the PC, decodes it, and then executes it. 
After execution, the PC is updated to point to the next instruction, ensuring continuous and correct program flow.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
## Native Method Stack

This memory is allocated for each thread when it is created and can have either a fixed or dynamic size. Native method stack is also known as C stack.
It is the dedicated memory region which supports the execution of the methods written in languages other than Java, such as C or C++. 
These methods are typically called through the Java Native Interface (JNI), which acts as a bridge between the JVM and native libraries.
When a native method is invoked, the JVM hands control over to the host operating system, which executes the method using the machine’s native call stack rather than the Java call stack.

Unlike the Java stack, which stores local variables, object references, and intermediate results for Java methods, the native method stack deals with:

    * Native language function calls
    * Operating system-level data structures
    * Registers and pointers specific to compiled native code.

The JVM doesn’t manage the internals of this stack in the same way it manages Java method execution. Instead, it delegates the execution entirely to the native system runtime, allowing native code to execute as if it were part of a regular C/C++ program.

The JVM provides a built-in feature to monitor native memory consumption with:

```
-XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions
```

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


