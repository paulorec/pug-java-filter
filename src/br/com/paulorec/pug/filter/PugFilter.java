package br.com.paulorec.pug.filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.neuland.jade4j.Jade4J;

public class PugFilter implements Filter {

	private FilterConfig filterConfig;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse))
			throw new UnsupportedOperationException("only http requests supported");

		if (response.getCharacterEncoding() == null) {
			response.setCharacterEncoding("UTF-8");
		}

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		File file = fetchFile(httpServletRequest);

		Jade4J.render(file.getAbsolutePath(), null);

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		this.filterConfig = filterConfig;
	}

	private File fetchFile(HttpServletRequest request) throws FileNotFoundException {

		ServletContext servletContext = filterConfig.getServletContext();

		String requestURI = request.getRequestURI();

		String requestPath = requestURI;

		String contextPath = request.getContextPath();

		if (!contextPath.equals("/")) {
			requestPath = requestPath.substring(contextPath.length());
		}

		String realPath = servletContext.getRealPath(requestPath);

		if (realPath == null) {
			throw new FileNotFoundException(requestPath);
		}

		realPath = realPath.replace('\\', '/');

		File file = new File(realPath);

		if (!file.exists()) {
			throw new FileNotFoundException(requestPath);

		}

		return file;
	}

}
