package src.algoritmos;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class VizinhoMaisProximo {

	public static void main(String[] args) throws IOException {

		/* LEITURA DO ARQUIVO DE ENTRADA */
		Scanner reader = new Scanner(System.in);
		List<String> fileLines = new ArrayList<String>();
		fileLines = readFile(reader, fileLines);
		reader.close();

		long tempoInicio = System.currentTimeMillis();
		
		/* IDENTIFICA NUM DE VERTICES E TIPO DE MATRIZ */
		int numVertices = getN(fileLines);
		int type = getType(fileLines);
		
		List<Double[]> matrizRetorno = null;
		Boolean entradaProcessada = true;
		
		switch (type) {
			case 1:
				matrizRetorno = type1(fileLines, numVertices);
				break;
			case 2:
				matrizRetorno = type2(fileLines, numVertices);
				break;
			case 3:
				matrizRetorno = type3(fileLines, numVertices);
				break;
			default:
				System.out.println("-- Tipo de matriz não identificado no arquivo. --");
				entradaProcessada = false;
		}
		
		if (entradaProcessada) { // Se o arquivo estiver com a entrada bem especificada
			
			ArrayList<ArrayList<Double>> matrizDeDistancias = new ArrayList<>();

			// CONVERSAO DE LIST<DOUBLE[]> PARA LIST<LIST<DOUBLE>>
			for(int i = 0; i < numVertices; i++){
				ArrayList<Double> linha = new ArrayList<>();
				for(Double num: matrizRetorno.get(i)) {
					linha.add(num);
				}
				matrizDeDistancias.add(linha);
			}
			// FIM_CONVERSAO
			
			
			/* INICIO DO ALGORITMO DE VIZINHO MAIS PROXIMO COM K PARTIDAS */
			
			int K = 0; // K = numero de partidas
			Random geradorDeAleatorio = new Random();
			
			
			/* K deve ter um valor entre K/3 e K/2.
			 * Escolhemos 5/12, por equivaler ao valor medio de 1/3 e de 1/2.
			 * Caso o resultado seja nao exato, somamos com 0,4 e aplicamos round() - equivale a arrendondar para cima.
			 */
			double kPontoFlutuante = (5.0*numVertices)/12.0;
			K = (int) Math.round(kPontoFlutuante + 0.4);
			
			
			ArrayList<Integer> cicloHamiltonianoMinimo = new ArrayList<>();
			Double custoCicloHamilttonianoMinimo = Double.MAX_VALUE;
			
			for (int partida = 0; partida < K; partida++) {
				
				/* Copia da matriz de distancias necessaria por conta das alteracoes a cada partida. */
				ArrayList<ArrayList<Double>> matrizDeDistanciasAlgoritmo = new ArrayList<ArrayList<Double>>();
				for (int linhaMatriz = 0; linhaMatriz < numVertices; linhaMatriz++) {
					ArrayList<Double> linhaCopia = new ArrayList<Double>();
					for (int colunaMatriz = 0; colunaMatriz < numVertices; colunaMatriz++) {
						linhaCopia.add(matrizDeDistancias.get(linhaMatriz).get(colunaMatriz));
					}
					matrizDeDistanciasAlgoritmo.add(linhaCopia);
				} // FIM DA COPIA
				
				ArrayList<Integer> cicloHamiltoniano = new ArrayList<>();
				Double custoCicloHamilttoniano = 0.0;
				
				// Na matriz, o vertice 1 será representado na linha 0.
				int indiceVerticeDePartida = geradorDeAleatorio.nextInt(numVertices);
				cicloHamiltoniano.add(indiceVerticeDePartida + 1);
				
				// Zera distancias da coluna do vertice mais proximo, para ser desconsiderado nas proximas iteracoes.
				for (int j = 0; j < numVertices; j++) {
					matrizDeDistanciasAlgoritmo.get(j).set(indiceVerticeDePartida, 0.0);
				}
				
				int indiceVizinhoMaisProximo = numVertices; // Garante que nao tera nenhum vertice com esse indice
				int indiceVerticeSendoVisitado = indiceVerticeDePartida;
								
				// Laco executado a cada visita de vertice restante
				for (int i = 0; i < numVertices-1; i++) { 
					
					// Inicializacao
					Double distanciaVizinhoMaisProximo = Double.MAX_VALUE;

					// Percorre as distancias dos vizinhos e guarda o mais proximo.
					for (int indiceColuna = 0; indiceColuna < numVertices; indiceColuna++) {
						Double distanciaVizinhoAtual = matrizDeDistanciasAlgoritmo.get(indiceVerticeSendoVisitado).get(indiceColuna);
						if (distanciaVizinhoAtual != 0) {
							if (distanciaVizinhoAtual < distanciaVizinhoMaisProximo) {
								distanciaVizinhoMaisProximo = distanciaVizinhoAtual;
								indiceVizinhoMaisProximo = indiceColuna;
							}
						}
					}
					
					// IMPRIMIR TESTE
//					System.out.println("ITERACAO Nº: " + (i+1));
//					for (int linhaTeste = 0; linhaTeste < numVertices; linhaTeste++) {
//						for (int colunaTeste = 0; colunaTeste < numVertices; colunaTeste++) {
//							System.out.print(matrizDeDistanciasAlgoritmo.get(linhaTeste).get(colunaTeste).toString() + " ");
//						}
//						System.out.println();
//					}
//					System.out.println();
//					System.out.println();

				
					// Zera distancias da coluna do vertice mais proximo, para ser desconsiderado nas proximas iteracoes.
					for (int j = 0; j < numVertices; j++) {
						matrizDeDistanciasAlgoritmo.get(j).set(indiceVizinhoMaisProximo, 0.0);
					}
					
					cicloHamiltoniano.add(indiceVizinhoMaisProximo + 1);
					custoCicloHamilttoniano += distanciaVizinhoMaisProximo;
					indiceVerticeSendoVisitado = indiceVizinhoMaisProximo;
					
				}
				
				cicloHamiltoniano.add(indiceVerticeDePartida+1); // Fechando o ciclo
				//Incrementa o custo total com o custo do ultimo vertice visitado para o vertice de partida.
				custoCicloHamilttoniano += matrizDeDistancias.get(indiceVerticeSendoVisitado).get(indiceVerticeDePartida);
				
				System.out.println("###############################################################################\n\n");				
				System.out.println("CH: " + cicloHamiltoniano.toString());
				System.out.println("CT: " + custoCicloHamilttoniano.toString()+ "\n\n");
				System.out.println("-------------------------------------------------------------------------------");
				
				if (custoCicloHamilttoniano < custoCicloHamilttonianoMinimo) {
					custoCicloHamilttonianoMinimo = custoCicloHamilttoniano;
					cicloHamiltonianoMinimo = cicloHamiltoniano;
				}
			}
			
			System.out.println("###############################################################################\n\n");				
			System.out.println("CH: " + cicloHamiltonianoMinimo.toString());
			System.out.println("CT: " + custoCicloHamilttonianoMinimo.toString()+ "\n\n");
			System.out.println("###############################################################################");
		
			long tempoExecucao = System.currentTimeMillis() - tempoInicio;
			System.out.println(tempoExecucao);
			
			/* FIM DO ALGORITMO DE VIZINHO MAIS PROXIMO COM K PARTIDAS */
			
		}

		System.out.println();
	}

	/* LÊ ARQUIVO E RETORNA UMA LISTA DE STRINGS, ONDE CADA ITEM É UMA LINHA DO ARQUIVO */
	private static List<String> readFile(Scanner reader, List<String> fileLines) throws IOException{
		int maxTries = 3, i = 0;
		while(true){
			try{
				System.out.print("Digite o nome do arquivo: ");
				String filename = reader.nextLine();
				if(filename.endsWith(".txt"))
					filename = filename.substring(0, filename.length()-4);
				fileLines = Files.readAllLines(Paths.get(".\\src\\testes\\" + filename.toLowerCase() + ".txt"), Charset.defaultCharset());
				break;
			}catch(IOException e){
				if(i < maxTries-1){
					System.out.println("-- Arquivo não existe. Tentativas restantes (" + (maxTries - ++i) + ") --");
					
				}
				else{
					System.err.println("\nMáximo de tentativas excedido. Reinicie o programa.\n");
					throw e;
				}
			}
		}
		int lineIndex = 0;
		for(String line : fileLines)
			fileLines.set(lineIndex, fileLines.get(lineIndex++).trim().replaceAll(" +", " "));
		return fileLines;
	}

	/* IDENTIFICA O NUM. DE VERTICES DO GRAFO PELA LEITURA DA PRIMEIRA LINHA DO ARQUIVO DE ENTRADA */
	private static int getN(List<String> fileLines) {
		int indexNStart = fileLines.get(0).indexOf("N=")+2;
		int indexNEnd = fileLines.get(0).indexOf(" ", indexNStart);
		int n = Integer.parseInt((fileLines.get(0).substring(indexNStart, indexNEnd)));
		return n;
	}

	 /* IDENTIFICA O TIPO DE MATRIZ DO GRAFO PELA LEITURA DA PRIMEIRA LINHA DO ARQUIVO DE ENTRADA */
	private static int getType(List<String> fileLines) {
		int indexTipoStart = fileLines.get(0).indexOf("Tipo=")+5;
		int indexTipoEnd = fileLines.get(0).indexOf(" ", indexTipoStart);
		int type = Integer.parseInt((fileLines.get(0).substring(indexTipoStart, indexTipoEnd)));
		return type;
	}
	
	/* RETORNA A DISTANCIA ENTRE DOIS PONTOS, SEGUINDO O TEOREMA DE PITAGORAS */
	private static Double DistanciaEntrePontos(Double[] coord1, Double[] coord2) {
		return Math.sqrt(Math.pow(coord2[0]-coord1[0], 2.0) + Math.pow(coord2[1]-coord1[1], 2.0));
	}

	/* ORGANIZA MATRIZ A PARTIR DE ENTRADA DO TIPO 1 - SIMÉTRICA */
	private static List<Double[]> type1(List<String> fileLines, int n) {

		List<Double[]> custos = new ArrayList<Double[]>();
		Double[] line = new Double[n];
		
		
		for(int i = 1; i < n; i++){
			int j = 0;
			while(i > j + 1)
				line[j] = custos.get(j++)[i-1];
			line[j++] = 0.0;

			for(String value: fileLines.get(i).split(" +|\t")){
				line[j++] = Double.parseDouble(value);
			}
			custos.add(line.clone());

		}
		for(int j = 0; j < n-1; j++)
			line[j] = custos.get(j)[n-1];
		line[n-1] = 0.0;
		custos.add(line.clone());
		
		return custos;
	}

	/* ORGANIZA MATRIZ A PARTIR DE ENTRADA DO TIPO 2 - COORDENADAS XY DO PLANO CARTESIANO */
	private static List<Double[]> type2(List<String> fileLines, int n) {
		List<Double[]> custos = new ArrayList<Double[]>();
		List<Double[]> coordenadas = new ArrayList<Double[]>();
		
		for(int i = 1; i <= n; i++){
			Double[] line = new Double[2];
			int j = 0;
			for(String value: fileLines.get(i).split(" +|\t")){
				line[j++] = Double.parseDouble(value);
			}
			coordenadas.add(line.clone());
		}
		
		for(int i = 0; i < n; i++){
			int j = 0;
			Double[] line = new Double[n];
			while(i > j + 1)
				line[j] = custos.get(j++)[i-1];
			line[j++] = 0.0;
			while(j < n)
				line[j] = DistanciaEntrePontos(coordenadas.get(i), coordenadas.get(j++));
			custos.add(line.clone());
		}
		
		return custos;
	}

	/* ORGANIZA MATRIZ A PARTIR DE ENTRADA DO TIPO 3 - MATRIZ NÃO-SIMÉTRICA */
	private static List<Double[]> type3(List<String> fileLines, int n) {
		List<Double[]> custos = new ArrayList<Double[]>();
		Double[] line = new Double[n];
		
		int j = 0;
		int valuesRead = 0;
		for(String entry: fileLines){
			if(entry.equals(fileLines.get(0)))
				continue;
			else if(entry.isEmpty())
				break;
			for(String value: entry.split(" +|\t")){
				line[j++] = Double.parseDouble(value);
				valuesRead++;
			}
			if(valuesRead == n){
				custos.add(line.clone());
				j = 0;
				valuesRead = 0;
			}
		}
		
		return custos;
	}

}
