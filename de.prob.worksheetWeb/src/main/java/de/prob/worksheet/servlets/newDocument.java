package de.prob.worksheet.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.prob.worksheet.WorksheetDocument;
import de.prob.worksheet.block.JavascriptBlock;

@WebServlet(urlPatterns={"/newDocument"})
public class newDocument extends HttpServlet {
	private static final long serialVersionUID = -8455020946701964097L;
	Logger logger = LoggerFactory.getLogger(newDocument.class);


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		this.logParameters(req);
		resp.setCharacterEncoding("UTF-8");
		// initialize the session
		this.setSessionProperties(req.getSession());

		// Load the session attibutes
		HashMap<String, Object> attributes = this.getSessionAttributes(req.getSession(), req.getParameter("worksheetSessionId"));

		
		// load or create the document
		WorksheetDocument doc = this.getDocument(attributes);
		attributes = new HashMap<String, Object>();
		attributes.put("document", doc);

		// store the session attributes
		this.setSessionAttributes(req.getSession(), req.getParameter("worksheetSessionId"), attributes);

		// print the json string to the response
		final ObjectMapper mapper = new ObjectMapper();
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		resp.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc));
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getSessionAttributes(HttpSession session, String wsid) {
		HashMap<String, Object> attributes = (HashMap<String, Object>) session.getAttribute(wsid);
		if (attributes == null){
			attributes = new HashMap<String, Object>();
			logger.debug("New 'Sub'session initialized with id :"+wsid);
		}
		logger.debug("Session attributes: "+attributes.toString());
		return attributes;
	}

	private void setSessionAttributes(HttpSession session, String wsid, HashMap<String, Object> attributes) {
		logger.debug("Session attributes: "+attributes.toString());
		session.setAttribute(wsid, attributes);
	}

	private void setSessionProperties(HttpSession session) {
		if (session.isNew()) {
			logger.debug("New Session initialized");
			session.setMaxInactiveInterval(-1);
		}
	}

	private WorksheetDocument getDocument(HashMap<String, Object> attributes) {
		WorksheetDocument doc = (WorksheetDocument) attributes.get("document");
		if (doc == null) {
			//TODO add distinction between eclipse plugin mode and external Browser mode (e.g. remove unnecessary menus in plugin mode)
			doc = new WorksheetDocument();
			doc.setId("ui-id-1");

			logger.debug("New WorksheetDocument initiaized");
					
			final JavascriptBlock block1 = new JavascriptBlock();
			doc.insertBlock(0, block1);
		}
		return doc;

	}
	private void logParameters(HttpServletRequest req){
		String[] params={"worksheetSessionId"};
		String msg="{ ";
		for(int x=0;x<params.length;x++){
			if(x!=0)msg+=" , ";
			msg+=params[x]+" : "+req.getParameter(params[x]);
		}
		msg+=" }";
		logger.debug(msg);
		
	}
}
