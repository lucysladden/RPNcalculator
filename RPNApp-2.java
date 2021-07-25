package week10;                                           

import java.util.*;                                            

/** RPNApp Class.
 *  A class that takes input from the user,
 *  and acts on it according to the assignment
 *  specifications.
 *  @author Hancock, Sladden, Biggs.
 */
public class RPNApp{                                            

    /** Our scanner object for user input. */
    private static Scanner scan = new Scanner(System.in); 
    /** Our array list for iterating through user input easily. */
    private static ArrayList<String> arrayRep = new ArrayList<String>();
    /** Our stack for performing calculations. */
    private static Stack<Integer> stack = new Stack<Integer>();   
    /** Our index int, this is a global variable because it is updated
     *  in various methods. */        
    private static int index; 

    /** Our main method. 
     *  Takes input from user, while it has a next
     *  line, processes each line, outputs the result in
     *  iterate array.
     *
     *  Also checks for matching parentheses here, delegates
     *  to external method.
     *  @param args to make main method work.
     */
    public static void main(String []args){
        String input = "";
        while (scan.hasNextLine()){
            input = scan.nextLine();
            String[] temp = input.split(" ");
            if (! matchParentheses(temp)){
                System.out.println("Error: unmatched parentheses");
            }else{ 
                for (int i = 0; i < temp.length; i++){
                    arrayRep.add(temp[i]);
                }
                iterateArray(toArrString(arrayRep));
            }
        }  
    }

    /** Method that turns an arrayList of type String
     *  into an array of type String.
     *  @param x is the input arrayList that we're changing.
     *  @return array of type String.
     */
    public static String[] toArrString(ArrayList<String> x){
        String[] ret = new String[x.size()];
        for (int i = 0; i < ret.length; i++){
            ret[i] = x.get(i);
        }
        return ret;
    }

    /** Method to evaluate -!, *!, etc.
     *  Take the size of stack, minus one to find number
     *  of operations, then call evaluate on that operation
     *  before the exclamation point as many times as is needed.
     *  @param tok is the token.
     */
    public static void evaluateExclamation(String tok){
        int iterationReq = stack.size()-1;
        for (int i = 0 ; i < iterationReq; i++){
            evaluate(tok.substring(0,1));
        }
        index++;
    }      

    /** Evaluate method.
     *  Performs all operations on stack.
     *
     *  Firstly, checks if it is an exclamation operation,
     *  acts accordingly by calling sister method, evaluate2,
     *  to operate (token)! until the stack size is 2.
     *
     *  For other operands, will pop off top and then evaluate
     *  each on a case by case basis using a switch statement.
     *
     *  Due to how long/complex Parentheses and roll code is, we've
     *  put it in another method.
     *
     *  Try-catch block catches division by zero and too
     *  few operands error (empty stack exception).
     *  
     *  @param token is the operand to act on.
     */
    public static void evaluate(String token){
        if (isExclamation(token)){
            evaluateExclamation(token);
        }
        try{
            int top = stack.pop();
            switch (token){
                case "+":
                    int next = stack.pop();
                    stack.push(next+top);
                    break;
                case "-":
                    next = stack.pop();
                    stack.push(next-top);
                    break;
                case "*":
                    next = stack.pop();
                    stack.push(next*top);
                    break;
                case "%":
                    next = stack.pop();
                    stack.push(next%top);
                    break;
                case "/":
                    next = stack.pop();
                    stack.push(next/top);
                    break;
                case "o":
                    next = stack.pop(); 
                    System.out.print(top + " ");
                    stack.push(next);
                    stack.push(top);
                    break;
                case "d":
                    stack.push(top);
                    stack.push(top);
                    break;
                case "c":
                    if (top < 0){
                        System.out.println("Error: negative copy");
                        stack.removeAllElements();
                    }else{
                        next = stack.pop();
                        for (int i = 0; i < top; i++){
                            stack.push(next);
                        }
                    }
                    break;
                case "r":
                    stack.push(top);
                    evaluateRoll();
                    break;
                case "(":
                    stack.push(top);
                    evaluateParentheses();
                    break;
            }
            if (!arrayRep.isEmpty()){
                if (isExclamation(arrayRep.get(index-1))){
                    stack.push(top);
                }
            }
        }catch (EmptyStackException e){
            System.out.println("Error: too few operands");
        }catch (ArithmeticException e){
            System.out.println("Error: division by 0");
        }
    }

    /** Delegated method to evaluate roll operand.
     */
    public static void evaluateRoll(){
        int top = stack.pop();
        if (top < 0){
            System.out.println("Error: negative roll");
            stack.removeAllElements();
        } else{
            int next = stack.pop();
            int k = top;
            stack.push(next);
            int[] tempStackRep = new int[k];
            for (int i = 0; i < k; i++){
                tempStackRep[i] = stack.pop();
            }
            stack.push(next);
            for (int i = k-1; i > 0; i--){
                stack.push(tempStackRep[i]);
            }
        }
    }

    /** Delegated method to evaluate Parentheses operand.
     *  Starts by checking if parentheses are nested.
     *  If they are, it changes the arrayRep into an equivalent
     *  form but without the widest most nest parentheses.
     *  Should continue this until there are no nested parentheses.
     *
     *  It then evaluates this by finding position of ")",
     *  creating a temporary String[] array that holds
     *  the operands/operators within parentheses.
     *
     *  Then acts on these like normal.
     */
    public static void evaluateParentheses(){
        while(isNested(toArrString(arrayRep))){
            turnParentheses(toArrString(arrayRep));
        }
        if (!arrayRep.isEmpty()){
            int top = stack.pop();
            int next = stack.pop();
            int m = top;
            stack.push(next);
            int closePos = index;
            while (!arrayRep.get(closePos).equals(")")){
                
                closePos++;
            }
            String[] hold = new String[(closePos-index-1)];
            for (int i = 0; i < hold.length; i++){
                hold[i] = arrayRep.get(index + 1);
                index++;
            }
            index ++;
            int iter = 0;
            while(iter < m){
                for (int i = 0; i < hold.length; i++){
                    if (isInteger(hold[i])){
                        stack.push(Integer.parseInt(hold[i]));
                    }else{
                        evaluate(hold[i]);
                    }
                }
                iter++;
            }
        }
    }

    /** isExclamation method that determines whether
     *  an operator is *!, *+, *-, etc.
     *  @param s is the String as part of the arrayRep
     *  we will be checking.
     *  @return true or false based on check.
     */
    public static boolean isExclamation(String s){
        return (s.length() == 2 && s.charAt(1) == '!');
    }

    /** isNested method to work out whether the arrayRep
     *  contains nested parentheses. We know if there are
     *  ever two ( in a row it will be nested.
     *  @param arrayVersion a String array of arrayRep.
     *  @return true or false for whether it is nested/
     */
    public static boolean isNested(String[] arrayVersion){
        int count = 0;
        for (int i = 0; i < arrayVersion.length; i++){
            if (arrayVersion[i].equals("(")){
                count++;
                if (count > 1){
                    return true;
                }
            }else if (arrayVersion[i].equals(")")){
                count--;
            }
        }
        return false;
    }

    /** nestedPosition method works out the position of the
     *  widest brackets of the nested part of array, so that
     *  we can remove them in turnParentheses method.
     *  @param arrayVersion is a String[] array representation
     *  of arrayRep.
     *  @return array of type int with effectively two coordinates.
     *  int[0] is for the ( position, int[1] is ) position.
     */
    public static int[] nestedPosition(String[] arrayVersion){
        int[] x = new int[2];
        int count = 0;
        int i = 0;
        while (true){
            if (arrayVersion[i].equals("(")){
                x[0] = i;
                break;
            }else{
                i++;
            }
        }
        int j = arrayVersion.length-1;
        while (true){
            if (arrayVersion[j].equals(")")){
                x[1] = j;
                break;
            }else{
                j--;
            }
        }
        return x;
    }

    /** turnParentheses method takes an array with nested
     *  parentheses and turns it into a longer array of equivalent
     *  form, that becomes the new arrayRep. It does this by using
     *  nestPosition to find where parentheses to be removed are,
     *  then it remembers m (described as k in assignment spec) and
     *  adds what was in the parentheses m times to arrayRep.
     *  @param shortVersion is a String array representation of arrayRep.
     */
    public static void turnParentheses(String[] shortVersion){
        arrayRep.removeAll(arrayRep);
        stack.removeAllElements();
        int[] nestedPos = nestedPosition(shortVersion);
        for (int i = 0; i < shortVersion.length; i++){
            if (i != nestedPos[0]){
                arrayRep.add(shortVersion[i]);
            }else{
                int m = Integer.parseInt(arrayRep.get(arrayRep.size()-1));
                arrayRep.remove(arrayRep.size()-1);
                for (int j = 0; j < m; j++){
                    for (int k = i+1; k < nestedPos[1]; k++){
                        arrayRep.add(shortVersion[k]);
                    }
                }
                i = nestedPos[1];
            } 
        }
        iterateArray(toArrString(arrayRep));
    } 

    /** matchParentheses method checks to see if we've
     *  got equivalent numbers of opening and closing brackets.
     *  does this using a simple counter that should end up being
     *  zero if the parentheses match up.
     *  @param check is a String version of arrayRep.
     *  @return true or false based on check.
     */
    public static boolean matchParentheses(String[] check){
        int count = 0;
        for (int i = 0; i<check.length; i++){
            if (check[i].equals("(")){
                count++;
            }else if (check[i].equals(")")){
                count--;
            }
        }
        return count == 0;
    }

    /** isOperator method, checks if a String is any of our defined operators.
     *  @param s is operator we're checking.
     *  @return true or false depending on check.
     */
    public static boolean isOperator(String s){
        return (s.equals("+") || s.equals("-") || s.equals("*")
                || s.equals("/") || s.equals("%") || s.equals("d")
                || s.equals("o") || s.equals("c") || s.equals("r")
                || s.equals("("));
    }

    /** isArithmetic method, checks if a String is any of our defined operators
     *  that is not "d" or "(".
     *  @param s is the operator we're checking.
     *  @return true or false depending on check.
     */
    public static boolean isArithmetic(String s){
        return (s.equals("+") || s.equals("-") || s.equals("*")
                || s.equals("/") || s.equals("%") || s.equals("o")
                || s.equals("c") || s.equals("r"));
    }

    /** isInteger method to determine if item
     *  in String array is number or not.
     *  @param input is the thing we're checking.
     *  @return true or false depending on
     *  if it's a number or not.
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /** Iterate array method to work our way through String array.
     *  Acts on whether item is int or operand.
     *
     *  @param objArr is an Array representation of ArrayList arrayRep
     *  that we are going to iterate through.
     */
    public static void iterateArray(String[] objArr){
        stack.removeAllElements();                                   
        for (index = 0; index < objArr.length; index++){
            if(isInteger(objArr[index])){
                stack.push((Integer.parseInt(objArr[index]))); 
            } else if (isOperator(objArr[index]) ||
                       isExclamation(objArr[index])){
                evaluate((objArr[index]));
            } else{
                System.out.println("Error: bad token '" + objArr[index] + "'");
                stack.removeAllElements();
            } 
        }
        if (!stack.empty()){
            System.out.println(stack.toString());
        }
        if (!arrayRep.isEmpty()){
            arrayRep.removeAll(arrayRep); 
        }
        if (!stack.empty()){
            stack.removeAllElements();   
        }
    }
}
