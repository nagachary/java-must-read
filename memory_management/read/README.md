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
    * Operand stack

Once a method completes execution, its associated stack frame is removed automatically.
Each thread’s stack is limited in size, which can be configured using the ```-Xss``` JVM option.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
## Method Area


