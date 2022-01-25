package org.lrth.springtests.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuredController {

  @GetMapping("/about")
  public String about() {
    return "about";
  }
  
  @GetMapping("/greeting")
  public String greeting(
    @AuthenticationPrincipal(expression = "username") String userName
  ) {
    return "Hi, " + userName;
  }
  
  @GetMapping("/submissions")
  public String submissions(
    @AuthenticationPrincipal UserDetails userDetails
  ) {
      return "Hi, " + userDetails.getUsername();
  }
  
  @GetMapping("/broadcast")
  public String getBroadcst(
    @AuthenticationPrincipal UserDetails userDetails
  ) {
      return "Hi, " + userDetails.getUsername();
  }
  
  @PostMapping("/broadcast")
  public String postBroadcst(
    @AuthenticationPrincipal UserDetails userDetails
  ) {
      return "Hi, " + userDetails.getUsername();
  }
}
