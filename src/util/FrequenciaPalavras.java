package util;

import java.util.List;
import java.util.Map;

public class FrequenciaPalavras {
	
	private Map<Long, String> frequecia;
	private List<Long> numeros;
	
	public FrequenciaPalavras() {
		super();
	}

	public Map<Long, String> getFrequecia() {
		return frequecia;
	}

	public void setFrequecia(Map<Long, String> frequecia) {
		this.frequecia = frequecia;
	}

	public List<Long> getNumeros() {
		return numeros;
	}

	public void setNumeros(List<Long> numeros) {
		this.numeros = numeros;
	}

}
