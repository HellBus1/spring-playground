package com.spring_playground.learning_core.controllers;

import java.util.Arrays;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessController {
  @GetMapping("/small-process")
  public String smallProcess() {
    int sum = 0;
    for (int i = 0; i < 1000; i++) {
      sum += i;
    }
    return String.format("Small process completed %s", sum);
  }

  @GetMapping("/medium-process")
  public String mediumProcess() {
    byte[] largeArray = new byte[1024 * 1024]; // 1 MB
    Arrays.fill(largeArray, (byte) 0);
    // Perform some operations on the array
    return "Medium process completed";
  }

  @GetMapping("/intensive-process")
  public String intensiveProcess() {
    // Recursive function to create large objects
    largeObject(10);
    return "Intensive process completed";
  }

  private void largeObject(int depth) {
    if (depth == 0) {
      return;
    }
    byte[] largeArray = new byte[1024 * 1024]; // 1 MB
    largeObject(depth - 1);
  }
}
