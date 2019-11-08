package com.gitenter.capsid.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.service.MemberService;
import com.gitenter.protease.domain.auth.MemberBean;

@RestController
@RequestMapping(value="/api")
public class RestMemberController {
	
	@Autowired MemberService memberService;

	@RequestMapping(value="/members/{memberId}", method=RequestMethod.GET)
	@ResponseBody
	public MemberBean showMember(@PathVariable Integer memberId) throws IOException {
		return memberService.getMemberById(memberId);
	}
}
