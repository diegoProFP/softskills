package es.ggm.infor.softskills.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.ggm.infor.softskills.controller.MainController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
public abstract class MainController {

    public static final String BASE_PATH = "/api/v1";
}
