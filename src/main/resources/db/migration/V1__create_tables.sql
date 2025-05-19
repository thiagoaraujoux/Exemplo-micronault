CREATE TABLE livros (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
titulo VARCHAR(255) NOT NULL,
autor VARCHAR(100) NOT NULL,
isbn VARCHAR(13) NOT NULL UNIQUE -- Garante que o ISBN seja único
);
CREATE TABLE usuarios (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(150) NOT NULL
);
CREATE TABLE emprestimos (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
livro_id BIGINT NOT NULL,
usuario_id BIGINT NOT NULL,
data_emprestimo DATE NOT NULL,

data_devolucao DATE, -- Pode ser nulo se o ainda não foi devolvido
ativo BOOLEAN NOT NULL DEFAULT TRUE, -- TRUE para empréstimo ativo
FOREIGN KEY (livro_id) REFERENCES livros(id),
FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);