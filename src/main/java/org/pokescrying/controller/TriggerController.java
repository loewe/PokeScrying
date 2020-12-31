package org.pokescrying.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TriggerController {
	@PostMapping("/trigger")
	@ResponseBody
	public void updateListings() {
		// read all raids and enabled raids and registered trainers and update the posts.
	}
}