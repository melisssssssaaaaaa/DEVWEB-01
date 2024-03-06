public class Main {
    public static void main(String[] args) {
        try {
            File input = new File("historico.html");
            Document doc = Jsoup.parse(input, "UTF-8");

            Elements historicos = doc.select("h1");
            for (Element historico : historicos) {
                System.out.println(historico.text());
                Element table = historico.nextElementSibling();
                Elements rows = table.select("tr");
                List<Disciplina> disciplinas = new ArrayList<>();

                for (int i = 1; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    Disciplina disciplina = new Disciplina();
                    disciplina.codigo = cols.get(0).text();
                    disciplina.nome = cols.get(1).text();
                    disciplina.professor = cols.get(2).text();
                    String[] notasStr = cols.get(3).text().split(",");
                    for (String notaStr : notasStr) {
                        disciplina.notas.add(Double.parseDouble(notaStr.trim()));
                    }
                    disciplina.faltas = Integer.parseInt(cols.get(4).text());
                    disciplinas.add(disciplina);
                }

                // Calcula média das notas
                for (Disciplina disciplina : disciplinas) {
                    System.out.println(disciplina.nome + ": " + disciplina.calcularMedia());
                }

                // Disciplina com mais faltas
                Disciplina disciplinaComMaisFaltas = disciplinas.stream()
                        .max(Comparator.comparingInt(d -> d.faltas))
                        .orElse(null);
                if (disciplinaComMaisFaltas != null) {
                    System.out.println("Disciplina com mais faltas: " + disciplinaComMaisFaltas.nome);
                }

                // Professor com mais faltas
                String professorComMaisFaltas = disciplinas.stream()
                        .collect(Collectors.groupingBy(d -> d.professor, Collectors.summingInt(d -> d.faltas)))
                        .entrySet().stream()
                        .max(Comparator.comparingInt(e -> e.getValue()))
                        .map(e -> e.getKey())
                        .orElse(null);
                if (professorComMaisFaltas != null) {
                    System.out.println("Professor com mais faltas: " + professorComMaisFaltas);
                }

                // Reagrupar por professor e datas crescentes (assumindo que datas são representadas por código de disciplina)
                disciplinas.sort(Comparator.comparing(d -> d.professor));
                disciplinas.forEach(d -> System.out.println(d.professor + " - " + d.codigo));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
