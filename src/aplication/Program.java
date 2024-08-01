package aplication;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Locale;
import java.util.Scanner;


import entity.Celular;
import entity.Cidade;
import entity.Cliente;
import entity.Endereco;
import entity.Estado;
import model.DAO.CelularDAO;
import model.DAO.CidadeDAO;
import model.DAO.ClienteDAO;
import model.DAO.EnderecoDAO;
import model.DAO.EstadoDAO;
import model.DAO.FabricaDAO;

public class Program {
		
	private static final int CADASTRAR_CLIENTE = 1;
	private static final int CADASTRAR_FILME = 2;
	private static final int VENDA_ALUGUEL = 3;
	private static final int CONSULTAR_FILME = 4;
	private static final int LISTAR_CLIENTE = 5;
	private static final int CONSULTAR_ID = 6;
	private static final int SAIR = 0;

	// fazer opções para consultar cidade, estado, celular, endereço e voltar ao menu principal

	public static void main(String[] args) {

		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);
		
		DateTimeFormatter dmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		Cliente cliente = null;
		Cidade cid = null;
		Endereco end = null;
		Estado est = null;
		Celular cel = null;

		System.out.println("--Sistema Locadora de Filmes IgaraTexas--");
		System.out.println("______________________________________________");
		int opcaoMenu = 99;
		System.out.println();

		while (opcaoMenu != SAIR) {
			System.out.println("--------Menu--------\n");
			System.out.println(" 1 - Cadastro de cliente;\n " + "2 - Cadastro de filme;\n "
					+ "3 - Venda ou Aluguel de filme;\n " + "4 - Consultar filme;\n " + "5 - Listar clientes;\n "
					+ "6 - Consultar cliente;\n " + "0 - Sair\n");
			System.out.print("Digite a opção desejada: ");
			opcaoMenu = sc.nextInt();
			sc.nextLine();
			switch (opcaoMenu) {
			case CADASTRAR_CLIENTE: {
				System.out.println("--Cadastro de Cliente--\n");

				boolean validarCPF = false;
				String cpf = null;
				while (!validarCPF) {
					System.out.print("Insira o CPF: ");
					cpf = sc.nextLine();
					if (Cliente.validarCPF(cpf) == true) {
						if(cpfExistente(cpf) == true) {
							validarCPF = true;
						}else {
							System.out.println("CPF existente!");
						}
					} else {
						System.out.println("CPF inválido!");
					}
				}

				boolean validarNome = false;
				String nome = null;
				while (!validarNome) {
					System.out.print("Insira o nome: ");
					nome = sc.nextLine();
					if (Cliente.validacaoNome(nome) == true) {
						validarNome = true;
					}
				}

				LocalDate data = null;
				boolean dataValida = false;
				while (!dataValida) {
					try {
						System.out.print("Insira a data de nascimento(dd/MM/yyyy): ");
						data = LocalDate.parse(sc.next(), dmt);
						sc.nextLine();
						dataValida = true;
					} catch (DateTimeParseException e) {
						System.out.println("Formato de data inválido! - " + e.getMessage());
					}
				}

				boolean validarCelular = false;
				String celular = null;
				while (!validarCelular) {
					System.out.print("Insira o número de celular para contato: ");
					celular = sc.nextLine();
					if (Celular.validarNumero(celular) == true) {
						if(cadastrarCelular(celular) != null) {
							validarCelular = true;
						}else {
							System.out.println("Celular existente!");
						}
					}
				}
				
				cel = cadastrarCelular(celular);
				
				String estado = null;
				boolean validarUf = false;
				while(!validarUf) {
					System.out.print("Insira o UF da cidade: ");
					estado = sc.nextLine();
					if(cadastrarEstado(estado) != null) {
						validarUf = true;
					}else {
						System.out.println("Estado existente!");
					}
				}
				
				est = cadastrarEstado(estado);
				
				String cidade = null;
				String cep = null;
				boolean validarCidade = false;
				while(!validarCidade) {
					System.out.print("Insira a cidade de residência: ");
					cidade = sc.nextLine();
					System.out.print("Insira o CEP da cidade: ");
					cep = sc.nextLine();
					if(cadastrarCidade(cidade, cep, est) != null) {
						validarCidade = true;
					}else {
						System.out.println("Cidade existente!");
					}
				}
				cid = cadastrarCidade(cidade, cep, est);
				
				String rua = null;
				String bairro = null;
				String numero = null;
				String complemento = null;
				boolean validarEndereco = false;
				while(!validarEndereco) {
					System.out.print("--Endereço--\nInsira a rua: ");
					rua = sc.nextLine();
					System.out.print("Insira o bairro: ");
					bairro = sc.nextLine();
					System.out.print("Insira o número da casa/apartamento: ");
					numero = sc.nextLine();
					System.out.print("Insira o complemento(casa/apartamento): ");
					complemento = sc.nextLine();
					if(cadastrarEndereco(rua, bairro, complemento, numero, cid) != null) {
						validarEndereco = true;
					}else {
						System.out.println("Endereço existente!");
					}
				}
				
				end = cadastrarEndereco(rua, bairro, complemento, numero, cid);
				
				cadastrarCliente(cpf, nome, data, cel,end);

			}
			default:
				// throw new IllegalArgumentException("Entrada inesperada: " + op);
			}
		}

		System.out.println(cliente);

		sc.close();
	}
	
	static ClienteDAO clienteDao = FabricaDAO.criarClienteDAO();
	static CelularDAO celularDao = FabricaDAO.criarCelularDAO();
	static EstadoDAO estadoDao = FabricaDAO.criarEstadoDAO();
	static CidadeDAO cidadeDao = FabricaDAO.criarCidadeDAO();
	static EnderecoDAO enderecoDao = FabricaDAO.criarEnderecoDAO();
	
	private static void cadastrarCliente(String cpf, String nome, LocalDate dataNascimento, Celular celular, Endereco endereco) {

		/*
		 * Period.between: Calcula o período entre a data de nascimento e a data atual.
		 * O método getYears() é usado para extrair a quantidade de anos completos desse
		 * período. LocalDate.now(): Obtém a data atual, necessária para calcular a
		 * diferença em anos entre a data de nascimento e o presente momento.
		 */
		LocalDate dataAtual = LocalDate.now();
		Period periodo = Period.between(dataNascimento, dataAtual);
		int idade = periodo.getYears();
		
		if (idade > 17) {
			
			Celular cel = new Celular(celular.getId(), null);
			Endereco end = new Endereco(endereco.getId(), null, null, null, null, null);
			
			clienteDao = FabricaDAO.criarClienteDAO();
			Cliente novoCliente = new Cliente(cpf, nome, dataNascimento, end, cel);
			clienteDao.inserir(novoCliente);
			System.out.println("\nCliente inserido! Novo ID = " + novoCliente.getCPF());
		} else {
			System.out.println("Idade inapropriada! Necessita de um responsável para efetuar o cadastro!");
		}
	}
	
	private static boolean cpfExistente(String cpf) {
		if(clienteDao.clienteExistente(cpf) != true) {
			return true;
		}else {
			return false;
		}
	}

	private static Celular cadastrarCelular(String numero) {
		celularDao = FabricaDAO.criarCelularDAO();
		Celular novoCelular = new Celular(null, numero);
		if(!celularDao.existe(novoCelular)) {
			celularDao.inserir(novoCelular);
			System.out.println("\nCelular inserido! Novo ID = " + novoCelular.getId());
			return novoCelular;
		}else {
			return celularDao.buscarCelularExistente(numero);
		}
		
	}
	
	private static Estado cadastrarEstado(String estado) {
		estadoDao = FabricaDAO.criarEstadoDAO();
		Estado novoEstado = new Estado(null, estado);
		if(!estadoDao.existe(novoEstado)) {
			estadoDao.inserir(novoEstado);
			System.out.println("\nEstado inserido! Novo ID = " + novoEstado.getId());
			return novoEstado;
		}else {
			return estadoDao.buscarEstadoExistente(estado);
		}
	}
	
	private static Cidade cadastrarCidade(String cidade, String cep, Estado uf) {
		cidadeDao = FabricaDAO.criarCidadeDAO();
		Cidade novaCidade = new Cidade(null, cidade, cep, uf);
		if(!cidadeDao.existe(novaCidade)) {
			cidadeDao.inserir(novaCidade);
			System.out.println("\nCidade inserida! Novo ID = " + novaCidade.getId());
			return novaCidade;
		}else {
			return cidadeDao.buscarCidadeExistente(cidade, cep, uf);
		}
	}
	
	private static Endereco cadastrarEndereco(String rua, String bairro, String complemento, String numero, Cidade cidade) {
		enderecoDao = FabricaDAO.criarEnderecoDAO();
		Endereco novoEndereco = new Endereco(null, cidade, rua, bairro, numero, complemento);
		if(!enderecoDao.existe(novoEndereco)) {
			enderecoDao.inserir(novoEndereco);
			System.out.println("\nEndereço inserido! Novo ID = " + novoEndereco.getId());
			return novoEndereco;
		}else {
			return enderecoDao.buscarEnderecoExistente(rua, bairro, numero, complemento, cidade);
		}
	}
}
