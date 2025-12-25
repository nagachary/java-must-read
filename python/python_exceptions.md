### Python Exceptions:

1. An exception is an event which occurs during the execution of a program that disrupts the normal flow of the program instructions. It Is an unwanted event which we didn’t expect.
    ```
    1. IndentationError
    2. ValueError
    3. ZeroDivisionError
    4. SyntaxError
2. Use try: and except: to handle the exception in python.
3. Handling Exception:
    ```
    1. def handleException():
    2.     try:
    3.         val = int(input('enter a number'))
    4.         avg = 10/val
    5.         return avg
    6.     except ValueError: 
    7.         print('number is not entered')
    8.     except ZeroDivisionError:
    9.         print('number entered cannot be zero')
    10.     except:
    11.         print('unexpected error occurred.')
    12. O/p: handleException()
4. BaseException >> Exception >> SystemExit >> KeyboardInterrupt
    1. Exception >> ArithmeticError >> LookupError  >> TypeError >> ValueError
    2. ArithmeticError >> ZeroDivisionError 
    3. LookupError >> IndexError >> ValueError 
5. Exceptions are propagated rough functions.
    ```
    1. def get_day(user_info):
    2.     day = int(input('please enter day'))
    3.     user_info.append(day)
    4.     
    5. def get_month(user_info):
    6.     month = int(input('please enter month'))
    7.     user_info.append(month)
    8. 
    9. def get_year(user_info):
    10.     year = int(input('please enter year'))
    11.     user_info.append(year)
    12. 
    13. def get_date_of_birth(user_info):
    14.     try:
    15.         get_day(user_info)
    16.         get_month(user_info)
    17.         get_year(user_info)
    18.         print('date of birth :', user_info)
    19.     except ValueError:
    20.         print('invalid day or month or year entered')
    21.     except:
    22.         print('un-expected error occurred')
6. Assertions are used for debugging or testing and also for documenting the code.
7. Assertions are assumptions in our program that should always be true. If the assumption is true python moves to execute next statement, otherwise it prints the error mentioned after the assert.
    ```
    1. def assert_function(number):
    2.     assert (number != 0), 'Entered 0 as number!'
    3.     return 1/number
    4. assert_function(10)  
8. Do not use assertions to validate input with assertions, like sanity checks.
9. Do not handle AssertionErrors in `try ..except`


Previous: [Python Functions](./python_functions.md) 
