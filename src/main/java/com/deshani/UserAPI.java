package com.deshani;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by deshani on 8/1/17.
 */


@Controller
@EnableAutoConfiguration
@RequestMapping("staticScanner/")
public class UserAPI {

    private static String productPath = "/home/deshani/Documents/IS/product-is";

    @RequestMapping(value = "dependencyCheck", method = RequestMethod.GET)
    @ResponseBody
    public String runDependencyCheckByGitURL() throws GitAPIException, MavenInvocationException, IOException {
        if (new File(productPath).exists()) {
            MainController.runDependencyCheck(productPath);
            return "success";
        }
        return "Product is not found";
    }

    @RequestMapping(value = "findSecBugs", method = RequestMethod.GET)
    @ResponseBody
    public String runFindSecBugsByGitURL() throws MavenInvocationException, IOException, ParserConfigurationException, SAXException, TransformerException, GitAPIException, URISyntaxException {
        if (new File(productPath).exists()) {
            MainController.runFindSecBugs(productPath);
            return "success";
        }
        return "Product is not found";
    }

    @RequestMapping(value = "cloneProduct", method = RequestMethod.GET)
    @ResponseBody
    public boolean gitClone(@RequestParam("gitUrl") String url, @RequestParam("branch") String branch) throws GitAPIException, IOException {
        return MainController.gitClone(url, branch, productPath);

    }

}

