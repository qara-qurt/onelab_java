package org.onelab.gateway_cli_service.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class AboutCLI {

    @ShellMethod(key = "about", value = "Информация о приложении: about")
    public String about() {
        return """
               🚀 Restaurant Gateway CLI
               📌 Версия: 1.0.0
               🔧 Использует: Kafka, Elasticsearch, Spring Shell
               💡 Введите 'help' для просмотра доступных команд.
               """;
    }
}
