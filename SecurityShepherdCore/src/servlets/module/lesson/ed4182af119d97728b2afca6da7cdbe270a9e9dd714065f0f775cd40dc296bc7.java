package servlets.module.lesson;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

import dbProcs.Getter;

import utils.FindXSS;
import utils.Validate;
import utils.XssFilter;

/**
 * Cross Site Request Forgery Lesson
 * Currently does not use user specific result key because of current CSRF blanket rule
 * <br/><br/>
 * This file is part of the Security Shepherd Project.
 * 
 * The Security Shepherd project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.<br/>
 * 
 * The Security Shepherd project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br/>
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Security Shepherd project.  If not, see <http://www.gnu.org/licenses/>. 
 * @author Mark Denihan
 *
 */
public class ed4182af119d97728b2afca6da7cdbe270a9e9dd714065f0f775cd40dc296bc7 extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(ed4182af119d97728b2afca6da7cdbe270a9e9dd714065f0f775cd40dc296bc7.class);
	/**
	 * User submission is parsed for a valid HTML IMG tag. The SRC attribute of this tag is then used to construct a URL object. This URL object is then checked to ensure a valid attack
	 * @param falseId User's session stored tempId
	 * @param messageForAdmin CSRF Submission
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException
	{
		log.debug("Cross-Site Request Forgery Lesson Servlet");
		PrintWriter out = response.getWriter();  
		out.print(getServletInfo());
		Encoder encoder = ESAPI.encoder();
		try
		{
			HttpSession ses = request.getSession(true);
			if(Validate.validateSession(ses))
			{
				Cookie tokenCookie = Validate.getToken(request.getCookies());
				Object tokenParmeter = request.getParameter("csrfToken");
				if(Validate.validateTokens(tokenCookie, tokenParmeter))
				{
					String falseId = (String) ses.getAttribute("falseId");
					log.debug("falseId = " + falseId);
					String messageForAdmin = request.getParameter("messageForAdmin").toLowerCase();
					log.debug("User Submitted - " + messageForAdmin);
					
					String htmlOutput = new String();
					boolean validLessonAttack = FindXSS.findCsrfAttackUrl(messageForAdmin, "/root/grantComplete/csrflesson", "userId", falseId);
					
					if(validLessonAttack)
					{
						htmlOutput = "<h2 class='title'>Well Done</h2>" +
								"<p>The administrator recieved your messege and submitted the GET request embbeded in it<br />" +
								"The result key for this lesson is <a>" +
								encoder.encodeForHTML(Getter.getModuleResultFromHash(getServletContext().getRealPath(""), this.getClass().getSimpleName())) +
								"</a>";
					}
					log.debug("Adding searchTerm to Html: " + messageForAdmin);
					htmlOutput += "<h2 class='title'>Message Sent</h2>" +
						"<p><table><tr><td>Sent To: </td><td>administrator@SecurityShepherd.com</td></tr>" +
						"<tr><td>Message: </td><td> " +
						"<img src=\"" + encoder.encodeForHTMLAttribute(messageForAdmin) + "\"/>" +
						"</td></tr></table></p>";
					log.debug("outputing HTML");
					out.write(htmlOutput);
				}
			}
		}
		catch(Exception e)
		{
			out.write("An Error Occured! You must be getting funky!");
			log.fatal("Cross Site Request Forgery Lesson - " + e.toString());
		}
		log.debug("End Cross-Site Request Forgery Lesson Servlet");
	}
}
