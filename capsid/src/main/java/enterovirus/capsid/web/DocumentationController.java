package enterovirus.capsid.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import enterovirus.enzymark.htmlgenerator.DefaultHtmlGenerator;
import enterovirus.enzymark.htmlgenerator.HtmlGenerator;
import lombok.Getter;

import org.springframework.ui.Model;

@Controller
public class DocumentationController {

	@RequestMapping("/about")
	public String about(Model model) throws Exception {

		List<NavigationNode> navigationPath = new ArrayList<NavigationNode>();
		navigationPath.add(new NavigationNode("Home", "/"));
		model.addAttribute("navigationPath", navigationPath);
		
		NavigationNode navigationEndNode = new NavigationNode("About", "/about"); 
		model.addAttribute("navigationEndNode", navigationEndNode);
		
		ClassLoader classLoader = getClass().getClassLoader();
		File about = new File(classLoader.getResource("markdown/about.md").getFile());
		
		HtmlGenerator htmlGenerator = new DefaultHtmlGenerator(about);
		model.addAttribute("content", htmlGenerator.getHtml());
		
		return "documentation/template";
	}
	
	/*
	 * This one is a "inner class" rather than "static nested class",
	 * as it is only used for "DocumentationController", so the nodes
	 * are really belongs to this DocumentController instance.
	 * 
	 * However, it cannot be private, because JSTL is using it.
	 */
	@Getter
	public class NavigationNode {
		
		private String name;
		private String link;
		
		public NavigationNode(String name, String link) {
			super();
			this.name = name;
			this.link = link;
		}
	}
}
