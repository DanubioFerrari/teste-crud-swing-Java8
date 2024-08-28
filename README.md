# teste-crud-swing-Java8
Testes referente a vaga de programador.
Teste de avaliação de maturidade de codficação

Desenvolver CRUD com interface gráfica contendo as seguintes informações:

Campos:

1) Descrição do Produto
- Tipo String.
- Minímo de 3 caracteres.
- Máximo de 80 caracteres.

2) Valor
- Tipo Moeda com mascara de formatação para 2 casas decimais.

3) EAN-13
- Tipo String.
- Minímo 13 caracteres.
- Máximo 13 caracteres.
- Validação para não permitir dígito verificador incorreto.

4) Status
- Tipo Select com opções (Ativo ou Inativo).
- Quando selecionado Inativo bloquear edição dos demais campos.

5) Percentual Imposto
- Tipo Percentual com mascara de formatação para 2 casas decimais.

6) Valor Imposto
- Tipo Moeda com mascara de formatação para 2 casas decimais.
- Calcular o valor do imposto com base nos campos Valor e Percentual Imposto.

Utilizar apenas as tecnologias abaixo:

Java 8;
Maven;
Banco de Dados Relacional (Preferencialmente: PostgreSQL);
Swing;

