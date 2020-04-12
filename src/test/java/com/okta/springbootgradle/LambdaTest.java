package com.okta.springbootgradle;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

interface SimpleLambda {
    public int sum(int x, int y);
}

public class LambdaTest {

    // create a lambda function with an var
    // encapsulted in its scope
    public SimpleLambda getTheLambda(int offset) {
        int scopedVar = offset;
        return (int x, int y) -> x + y + scopedVar;
    }

    @Test
    public void testClosure() {
        // get and test a lambda/closure with offset = 1
        SimpleLambda lambda1 = getTheLambda(1);
        assertEquals(lambda1.sum(2,2), 5);

        // get and test a lambda/closure with offset = 2
        SimpleLambda lambda2 = getTheLambda(2);
        assertEquals(lambda2.sum(2,2), 6);
    }

}
