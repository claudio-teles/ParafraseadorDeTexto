package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;

public class UtilitarioGeral {
	
	private String termoDeSaida = "";
	
	public String obterLinkRedecionamentoUrl(String url) throws IOException {
		String s = Jsoup.connect(url).followRedirects(true).execute().parse().toString();
		int inicio = s.indexOf("redirectUrl='") + "redirectUrl='".length();
		
		String link = s.substring(inicio, s.indexOf("';google"));
		return link;
	}
	
	public String geradorTermosPesquisa(String palavraChave, List<String> sinonimos) {
		termoDeSaida = "";
		termoDeSaida += "\""+palavraChave+"\"+";
		
		sinonimos.parallelStream().limit(5).forEach(palavra -> termoDeSaida += ("\""+palavra+"\"+"));
		
		String saida = URLEncoder.encode(termoDeSaida, StandardCharsets.UTF_8);
		termoDeSaida = "";
		return saida;
	}
	
	public FrequenciaPalavras obterFrequencia(List<String> sinonimos, List<String> itensContexto) {
		List<Long> n = new ArrayList<Long>();
		Map<Long, String> map1 = new HashMap<Long, String>();
		Map<String, Long> map2 = new HashMap<String, Long>();
		map2 = this.medirFrequenciaPalavras(sinonimos, itensContexto);
		
		map2.forEach((s, l) -> {
			map1.put(l, s);
			n.add(l);
		});// n.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).get(0)
		FrequenciaPalavras fn = new FrequenciaPalavras();
		fn.setFrequecia(map1);
		fn.setNumeros(n);
		return fn;
	}
	
	public String encontrarPalavraMaisFrequente(FrequenciaPalavras frequenciaPalavras) {
		return frequenciaPalavras.getFrequecia().get(frequenciaPalavras.getNumeros().stream().max(Long::compare).get());
	}
	
	public String sortearPalavra(FrequenciaPalavras frequenciaPalavras) {
		List<Long> numeros = new ArrayList<>();
		numeros.addAll(frequenciaPalavras.getNumeros().stream().limit(3).sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
		
		int quantidadeDeNumeros = numeros.size();
		
		if (quantidadeDeNumeros > 0) {
			if (quantidadeDeNumeros == 3) {
				// 3 valores mais altos do mapa, onde 1 dos 3 é sorteado 
				return frequenciaPalavras.getFrequecia().get(numeros.get(new Random().nextInt(3)));
			} else {
//				return frequenciaPalavras.getFrequecia().get(numeros.get(0));// valor mais alto do mapa
				return this.encontrarPalavraMaisFrequente(frequenciaPalavras);
			}
		}
		return null;
	}
	
	public Map<String, Long> medirFrequenciaPalavras(List<String> sinonimos, List<String> itensContexto) {
		Map<String, Long> frequenciaDePalavras = new HashMap<String, Long>();
		
		sinonimos.forEach(sinonimo -> {
			Long frequencia = itensContexto
										.stream()
										    .filter(item -> item.contains(sinonimo))
										 .count();
			
			frequenciaDePalavras.put(sinonimo, frequencia);
		});
		return frequenciaDePalavras;
	}
	
	public BufferedReader conexaoServidorSinonimos(String palavraPesquisada) throws MalformedURLException, IOException {
		URL url = new URL("https://www.sinonimos.com.br/"+palavraPesquisada);
		HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
		if (conexao.getResponseCode() != 200) {
			throw new RuntimeException("Código de erro HTTP : " + conexao.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
		return br;
	}

	public List<String> obterSinonimosOnline(String palavraPesquisada) throws Exception {
		List<String> listaDeSinonimos = new ArrayList<>();
		try {
			if (!palavraPesquisada.equals("") || palavraPesquisada != null) {
				BufferedReader br = conexaoServidorSinonimos(palavraPesquisada);

				String linha = br.lines().filter(f -> f.contains("class=\"sinonimo\">")).collect(Collectors.toList()).get(0);
				String subLinha = linha;

				int inicio = 0;
				int fim = 0;
				int posInicial = ("class=\"sinonimo\">".length());
				if (!linha.equals("") && linha != null) {
					while (subLinha.contains("class=\"sinonimo\">")) {
						inicio = subLinha.indexOf("class=\"sinonimo\">") + posInicial;
						fim = subLinha.indexOf("</a>");
						String palavra = subLinha.substring(inicio, fim);
						listaDeSinonimos.add(palavra);
						inicio += palavra.length() + 4;
						subLinha = subLinha.substring(inicio);
					}
				}
			}
		} catch (Exception e) {}
		return listaDeSinonimos;
	}
	
	
//	private int contarNumeroPalavrasSelecionadas(List<String> sinonimos, int tamanhoSinonimos, int quantidadeSorteada) {
//		String palavra = sinonimos.get(new Random().nextInt(tamanhoSinonimos));
//		if (!subListaSinonimos.contains(palavra)) {
//			subListaSinonimos.add(palavra);
//			quantidadeSorteada++;
//		}
//		return quantidadeSorteada;
//	}
	
//	public String geradorTermosPesquisa(String palavraChave, List<String> sinonimos) {
//		int tamanhoSinonimos = sinonimos.size();
//		int quantidadeSorteada = 0;
//		
//		termoDeSaida = "";
//		
//		termoDeSaida += "\""+palavraChave+"\"+";
//		
//		if (tamanhoSinonimos >= 10) {
//			while (quantidadeSorteada < 10) {
//				quantidadeSorteada = contarNumeroPalavrasSelecionadas(sinonimos, tamanhoSinonimos, quantidadeSorteada);
//			} 
//			subListaSinonimos.forEach(palavra -> {
//				termoDeSaida += ("\""+palavra+"\"+");
//			});
//		} else {
//			while (quantidadeSorteada < tamanhoSinonimos) {
//				quantidadeSorteada = contarNumeroPalavrasSelecionadas(sinonimos, tamanhoSinonimos, quantidadeSorteada);
//			} 
//			subListaSinonimos.forEach(palavra -> {
//				termoDeSaida += ("\""+palavra+"\"+");
//			});
//		}
//		subListaSinonimos.clear();
//		return URLEncoder.encode(termoDeSaida, StandardCharsets.UTF_8);
//	}

}
