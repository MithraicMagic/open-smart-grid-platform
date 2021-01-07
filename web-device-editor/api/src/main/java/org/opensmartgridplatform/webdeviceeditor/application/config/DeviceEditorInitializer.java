package org.opensmartgridplatform.webdeviceeditor.application.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class DeviceEditorInitializer extends SpringBootServletInitializer {

	public static void main(final String[] args) {
		SpringApplication.run(DeviceEditorInitializer.class, args);
	}

	@Override
	public void onStartup(final ServletContext servletContext) {
		final AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(DeviceEditorConfig.class, PersistenceConfig.class);
		ctx.setServletContext(servletContext);

		final ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
		servlet.setLoadOnStartup(1);
		servlet.addMapping("/");
	}
}
