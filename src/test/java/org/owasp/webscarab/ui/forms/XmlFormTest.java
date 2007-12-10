/**
 * 
 */
package org.owasp.webscarab.ui.forms;

import java.awt.Image;
import java.io.IOException;
import java.net.URI;

import org.owasp.webscarab.domain.Conversation;
import org.owasp.webscarab.domain.NamedValue;
import org.owasp.webscarab.ui.forms.ResponseForm;
import org.owasp.webscarab.ui.forms.support.ArrayChangeDetector;
import org.springframework.binding.form.ValidatingFormModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.richclient.application.support.DefaultApplicationServices;
import org.springframework.richclient.dialog.DialogPage;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.Form;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.AwtImageResource;
import org.springframework.richclient.image.DefaultIconSource;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * @author rdawes
 *
 */
public class XmlFormTest extends SpringRichTestCase {

	private Conversation c;
	
	private ValidatingFormModel model;
	
	private Form form;
	
	private DialogPage page;
	
	private TitledPageApplicationDialog dialog;
	
	private String content = "<tns:body xmlns:tns=\"http://jroller.org\">Here is link to my <a href=\"http://www.jroller.com/page/santhosh\">weblog</a>it contains some cool swing stuff. </tns:body>";
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(XmlFormTest.class);
	}

	protected void doSetUp() throws Exception {
		c = new Conversation();
		c.setRequestMethod("GET");
		c.setRequestUri(new URI("http://localhost/"));
		c.setRequestVersion("HTTP/1.0");
		c.setRequestHeader(new NamedValue("Host", "localhost"));
		c.setResponseVersion("HTTP/1.0");
		c.setResponseStatus("302");
		c.setResponseMessage("Moved");
		c.setResponseHeader(new NamedValue("Location",
				"http://localhost/index.html"));
		c.setResponseHeader(new NamedValue("Content-Type", "text/xml"));
		c.setResponseContent(content.getBytes());
	}

	protected void registerAdditionalServices(
			DefaultApplicationServices defaultapplicationservices) {
		try {
			ImageSource imageSource = new ImageSource() {
				AwtImageResource brokenImageIndicatorResource = new AwtImageResource(
						new ClassPathResource("images/alert/error_obj.gif"));

				Image brokenImageIndicator = brokenImageIndicatorResource
						.getImage();

				public Image getImage(String key) {
					return brokenImageIndicator;
				}

				public AwtImageResource getImageResource(String key) {
					return brokenImageIndicatorResource;
				}
			};

			defaultapplicationservices.setImageSource(imageSource);
			defaultapplicationservices.setIconSource(new DefaultIconSource(
					imageSource));
			defaultapplicationservices.setValueChangeDetector(new ArrayChangeDetector());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void showDialog() {
		model = FormModelHelper.createFormModel(new Conversation(), true);
		form = new ResponseForm(model);
		page = new FormBackedDialogPage(form);
		dialog = new TitledPageApplicationDialog(page, null) {
			protected void onAboutToShow() {
				setEnabled(page.isPageComplete());
			}
			
            protected void onInitialized() {
                model.setFormObject(c);
            }

            protected void onWindowGainedFocus() {
                model.setFormObject(c);
            }

            protected boolean onFinish() {
				System.out.println("Commit!");
				form.commit();
				return true;
			}
		};
		dialog.getDialog().setSize(800, 600);
		dialog.showDialog();
	}
	
	public void testXml() throws Exception {
	    System.out.println("Content-type is '" + c.getResponseHeader("Content-Type") + "'");
		showDialog();
	}

}
