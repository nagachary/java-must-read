### Python Functions:

1. A function define starts with a keyword ‘def’ and then a space and function name and () braces.
2. Functions of python can do two things 1) cause some effect. 2) return a meaningful value.
3. We cannot invoke a function before we define it.
4. Invoke a function using - function_name()
   ```
    1. def greet():
        1. print(‘this is a greet function’)
    2. calling a function : greet()
5. For a parameterized functions, when you invoke, you must pass all the arguments.
6. Order of arguments is important while invoking the function, they should be passed in the order of the definition of function parameters. The argument values are assigned to the parameters based on the position, they are positional arguments.
7. We can use the named arguments, by using names arguments we can pass the arguments in any order.
   ```` 
    1. def find_characters(text, letter):
    2.     count = 0;
    3.     for char in text:
    4.         if char == key_word:
    5.             count += 1
    6.     print('the letter', key_word, 'is repeated', count, 'no of times in', text)
    7. 
    8. find_characters(letter='p', text='apple pie')
8. print(‘hello’, ‘how are you’, sep=‘-‘, end=‘.’) => sep and end are named arguments.Default arguments are always optional and they will have default values. These are passed after positional arguments.
9. return keyword is used to return the function return value, any instruction after the return statement will be ignored and will not be executed.
    1. len return a value
    2. Input print and return a value
    3. print() print and return  None
10. None is a keyword which is neither True nor False or it doesn’t have any value. If no return is there then the function returns None. E.g: print() function returns None. None is a special kind of unique value in Python, it is described as no value or null value
    ```
    1. y = None
    2. if y is None:
    3.     print('y is None')
    4. O/p: y is None
11. == and is operators does the same comparison here.
12. Name scope: 
    ```
    1. wish = 'good morning'
    2. def greet():
    3.     global wish # here global variable wish is referred and updated 
    4.     wish = 'test morning'
    5.     print(wish)
    6. print(wish)
    7. greet()
    8. print(wish) 
    9. O/p: good morning
    10. test morning
    11. test morning
13. Can we define a variable which is used in a function outside the function call? The code works fine, because the variable that exists outside the function, at the time of the function call will have a scope inside the function body which means the function can see them and use them.
14. Shadowing:  Local variable and local variable: Local variable shadows the global variable which has the same name, but they are different variables, during the function call global variable is shadowed by local variable.
15. Due to shadowing feature, functions which has variables with the same name as a global variable, will not modify the global variable.
16.  global is a keyword which helps not to use shadowing, which means, we can update the global variable inside a function if we add global in-front of a variable name inside the function
17. Try to avoid global 
18. If you assign a new variable using the `=` the shadowing works, otherwise if  you append or insert a value to a variable (e.g. list type), shadowing doesn’t work.
19. Recursion: Recursion is a special technique for writing function, recursion takes place when function calls itself. e.g. for factorial() method
20. Generators: Generators are used to return lots of values 1 by 1. We use yield keyword to return the number of the generator. next(generator) will return the next number or element.
   ```
    1. def generator():
    2.     for i in range(1, 11):
    3.         yield i
    4. call generator: val = generator()
    5. next(val)
    6. 
    7. for i in generator():
    8.     print(i) 
```



Previous: [Python Collections](./python_collections.md)

Next: [Python Exceptions](./python_exceptions.md)