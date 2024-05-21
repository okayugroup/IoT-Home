package com.okayugroup.IotHome;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Resources {
    public final String parent = "/webroot";
    @GetMapping(parent + "/myresource")
    public String getIt() {
        LogController.LOG.addLog("/myResource accessed");
        return "Hello, World!";
    }
}
