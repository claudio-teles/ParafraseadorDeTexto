package manipulacao.documento;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class ManipulacaoDocumento {
	
	@SuppressWarnings("resource")
	public String obterConteudoArquivoDoc(String linkFinal) throws MalformedURLException, IOException {
		return new WordExtractor(new URL(linkFinal).openStream()).getText();
	}
	
	public String obterConteudoArquivoPDF(String linkFinal) throws MalformedURLException, IOException {
		InputStream is = new URL(linkFinal).openStream();
		PDDocument documento = PDDocument.load(is);
		String texto = new PDFTextStripper().getText(documento);
		documento.close();
		return texto;
	}
	
	@SuppressWarnings("resource")
	public String obterConteudoArquivoDocx(String linkFinal) throws URISyntaxException, IOException {
		InputStream is = new FileInputStream(new File(new URI(linkFinal).getPath()));
		XWPFDocument documento = new XWPFDocument(is);
		String texto = new XWPFWordExtractor(documento).getText();
		return texto;
	}
	
	public String lerDocWordOnline(ManipulacaoDocumento doc, String linkOriginal) throws IOException {
		return doc.obterConteudoArquivoDoc(linkOriginal);
	}
}
