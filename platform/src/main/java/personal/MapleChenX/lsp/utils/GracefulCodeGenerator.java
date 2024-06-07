package personal.MapleChenX.lsp.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GracefulCodeGenerator {
    public static int codeGenerate(){

        Set<Integer> set = new HashSet<>();
        Random random = new Random();

        while (set.size() < 4) {
            int digit = random.nextInt(1,10); // Generate a random digit between 0 and 9
            set.add(digit);
        }
        StringBuilder code = new StringBuilder();
        int i = 1;
        for (int digit : set) {
            code.append(digit);
            if(i == 2 || i == 5){
                code.append(digit);
                i++;
            }
            i++;
        }
        return Integer.parseInt(code.toString());
    }
}
