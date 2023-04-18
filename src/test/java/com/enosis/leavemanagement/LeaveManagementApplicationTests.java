package com.enosis.leavemanagement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class LeaveManagementApplicationTests {

	private Calculator calculator = new Calculator();
	@Test
	void addNumbers() {
		int x = 20;
		int y = 30;
		int result = calculator.add(x, y);
		int expected = 50;

		Assertions.assertEquals(expected, result);
	}

	class Calculator {
		public int add(int x, int y){
			return x+y;
		}
	}

}
