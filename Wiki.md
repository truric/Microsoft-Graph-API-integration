## Convenção de nomes ##

1. URIs devem começar com uma letra e usar apenas letras minúsculas. 

    ```json
    /users, /orders
    ```

2. Substantivos plurais devem ser usados na URI para identificar coleções de recursos.

    ```json
    /users, /orders
    ```

3. Literais/expressões em caminhos de URI devem ser separados usando um hífen (-) para facilitar a leitura.

    ```json
    /user-info, /order-history
    ```

4. Literais/expressões em caminhos de URI devem ser separados usando um hífen (-) para facilitar a leitura. 

    ```json
    /users?sort_by=name, /orders?filter_by_status=completed
    ```

5. As coleções de sub-recursos devem existir diretamente abaixo de um recurso individual para transmitir um relacionamento com outra coleção de recursos. 

    ```json
    /users/{user_id}/orders, /orders/{order_id}/items
    ```

6. Devemos apontar para o aninhamento limitado de recursos. 

    ```json
    /items/{item_id}
    ```

## Segurança

1. Os serviços DEVEM exigir o uso de HTTPS para o tráfego da API RESTful. Os serviços NÃO DEVEM redirecionar solicitações HTTP não seguras para seus equivalentes HTTPS seguros, mas devem resultar em uma falha grave.

2. Os serviços NÃO DEVEM usar certificados curinga ou autoassinados quando implantados na produção.

3. Os serviços DEVEM usar certificados emitidos por autoridades de certificação amplamente confiáveis, exclusivamente, quando implantados em produção.

4. Os serviços DEVEM seguir as melhores práticas da indústria para proteger as chaves privadas. Isso inclui, mas não está limitado a, armazenar chaves apenas em sistemas de segurança reforçada e totalmente corrigidos.

5. Os serviços NÃO DEVEM ser executados como root.

6. Os serviços DEVEM executar a validação de entrada em todos os parâmetros de solicitação REST (consulte https://www.owasp.org/index.php/Top_10_2010-A1-Injection).

    ```  
    exemplo inválido:

    https://example.com/account/325365436/transfer?amount=$
    ```
7. A API REST DEVE utilizar a implementação OAuth2 exclusivamente para autenticação e autorização de usuários. Mecanismos específicos e diretrizes para uso desta implementação são definidos nos artefatos arquiteturais daquele projeto e documentação de padrões relacionados. Referências adicionais serão fornecidas aqui à medida que a implementação avança.

8. Onde qualquer diretriz neste documento diverge de um protocolo padrão empregado para autenticação ou autorização, o protocolo padrão DEVE ser seguido.

9. Um serviço NÃO DEVE aceitar material de autenticação ou tokens de autorização fornecidos como componentes de um caminho de URL de solicitação ou parâmetros de consulta.

## Autenticação e Autorização

1. A API REST DEVE utilizar a implementação OAuth2 exclusivamente para autenticação e autorização de usuários. Mecanismos específicos e diretrizes para uso desta implementação são definidos nos artefatos arquiteturais daquele projeto e documentação de padrões relacionados. Referências adicionais serão fornecidas aqui à medida que a implementação avança.

2. Onde qualquer diretriz neste documento diverge de um protocolo padrão empregado para autenticação ou autorização, o protocolo padrão DEVE ser seguido.

3. Um serviço NÃO DEVE aceitar material de autenticação ou tokens de autorização fornecidos como componentes de um caminho de URL de solicitação ou parâmetros de consulta.

## Representações

1. As representações de recursos DEVEM ser baseadas em padrões estabelecidos quando tais padrões existirem para o tipo de recurso.

2. Onde os padrões de representação de recursos não se aplicam, os recursos de dados estruturados DEVEM suportar atualização e recuperação com base em representações JSON. O tipo de mídia a ser usado em solicitações e respostas HTTP que contêm dados de entidade no formato JSON DEVE ser **application/json** e, opcionalmente, qualificado com um parâmetro **charset=UTF-8**. Ausente esta qualificação, no entanto, DEVE ser assumido que os dados da entidade são codificados como UTF-8.
    ```
    Tipo de conteúdo: aplicativo/json
    Tipo de conteúdo: aplicativo/json; conjunto de caracteres = UTF-8
    ```

3. Um identificador de recurso denominado "url" DEVE estar presente em todas as representações de recursos da API RESTful, cujo valor DEVE ser o URL absoluto e canônico do próprio recurso.
    ```json
    {
        url: https://widgets.example.com/files/v3/documents/e23af9"
        ...
    }
    ```

4. Os serviços PODEM suportar o retorno de representações amplas quando explicitamente solicitado pelo cliente e quando o tamanho máximo da representação retornada for limitado.

5. Os serviços NÃO DEVEM retornar representações estreitas ou amplas de recursos hospedados em outros serviços, mas DEVEM retornar apenas representações de referência desses recursos. Ou seja, um serviço NÃO DEVE agir como um proxy para o estado do recurso RESTful para o qual a fonte da verdade é outro serviço. As exceções DEVEM melhorar comprovadamente a experiência do usuário ou o desempenho do sistema. Em qualquer caso excepcional, um serviço DEVE fornecer um limite superior documentado para quanto tempo os dados armazenados em cache podem permanecer fora de sincronia.

    exemplo inválido:
    ```json
    {
        url: https://widgets.example.com/files/v3/documents/e23af9,
        name: Documento Incrível.docx,
        Comente: {
            url: https://widgets.example.com/files/v3/comments/a29f31,
            text: Uau, que documento incrível.
        }
        autor: {
            url: https://widgets.example.com/users/v3/authors/b569fe",
            primeiro: Benvólio,
            last: Montague
        }
    }
    ```
    Assumindo que a representação acima foi retornada pelo serviço Arquivos, ela é inválida porque o objeto "autor" que está hospedado no serviço Usuário remoto inclui o nome e o sobrenome do autor. Um equivalente válido do exemplo acima seria:

    exemplo válido:
    ```json
    {
        url: https://widgets.example.com/files/v3/documents/e23af9,	
        name: Documento Incrível.docx,	
        Comente: {		
            url: https://widgets.example.com/files/v3/comments/a29f31,			
            text: Uau, que documento incrível.,					
        }
        autor: {
            url: https://widgets.example.com/users/v3/authors/b569fe
        }
    }
    ```
    O acima é válido porque o autor é representado usando uma representação de referência.

6. Além do campo obrigatório "url", um serviço PODE incluir propriedades de identificação adicionais como parte da representação de um recurso, como um campo "id". Se presente, qualquer um desses campos DEVE ser anunciado aos clientes como opaco, não canonizável para o URL do recurso e potencialmente instável por um longo período de tempo.	

7. Campos de data e hora DEVEM ser representados como strings e formatados de acordo com RFC-3339, especificamente a especificação de sintaxe ABNF para iso-date-tipe.	

8. Campos de duração DEVEM ser representados como inteiros representando comprimentos de tempo em segundos inteiros, ou strings e formatados de acordo com RFC-3339, especificamente a especificação de sintaxe ABNF para duration.	

9. Os campos de intervalo temporal DEVEM ser representados como strings e formatados de acordo com a RFC-3339, especificamente a especificação de sintaxe ABNF para period.

10. Com relação aos nomes de propriedade de representação JSON e parâmetros de consulta de URL, os serviços DEVEM:
    - Escolha nomes significativos e sucintos,
    - Não reutilizar quaisquer nomes reservados para outros fins por estas diretrizes,
    - Evitar conflitos internos de nomenclatura reutilizando nomes para propósitos diferentes,
    - Use substantivos plurais para matrizes,
    - Use substantivos singulares para não-arrays,
    - Comece com letras minúsculas,
    - Prefira camelCase sobre under_scores,
    - Seguir a nomenclatura do SCIM Schema quando o campo representa dados do diretório,
    - Sensível a maiúsculas e minúsculas

11. Com relação aos tipos de atributos de representação JSON, os serviços DEVEM preferir o tipo booleano JSON nativo a strings contendo "verdadeiro", "falso", "sim", "não" etc.

12. O sucesso ou falha de uma solicitação de API DEVE ser refletido no código de resultado HTTP da resposta e, portanto, os serviços NÃO DEVEM retornar dados de entidade JSON em respostas HTTP com a finalidade de indicar o sucesso ou falha de uma solicitação de API, conforme isso seria redundante e potencialmente entraria em conflito com o código de resultado HTTP. No entanto, os dados da entidade JSON podem ser retornados de forma a aumentar o status fornecido no código de resultado HTTP, fornecendo detalhes adicionais, conforme apropriado para falhas de solicitação e descritos na seção (códigos de status).

## Formato de URL

1. A raiz de uma parte do caminho URL do recurso REST DEVE ser um nome registrado representando o serviço que hospeda o recurso. Os serviços PODEM incluir um nível adicional de identificação para representar um subconjunto, ou classe API, da API geral do serviço. Nomes de serviço e nomes de classe de API DEVEM ser escolhidos com cuidado, de modo que não precisem ser alterados quando os produtos forem alterados ou renomeados. O nome de host não qualificado (ou subdomínio de nível inferior) da parte do domínio de um URL de recurso REST PODE corresponder ao nome da API do serviço, caso contrário, DEVE ser "api".

    modelo:
    ```json
    https://{domínio}/{serviço}/{...}
    https://{domínio}/{serviço}/{apiclass}/{...}
    ```

    exemplos:
    ```json
    https://widgets.example.com/files/{...}
    https://api.example.com/contacts/scim/{...}
    ```

2. Um recurso PODE ser acessível através de qualquer número de URLs, no entanto, um recurso DEVE ter um URL canônico. O URL canônico de um recurso NÃO DEVE conter tokens dependentes de contexto (por exemplo, **@me**) e NÃO DEVE conter parâmetros de consulta ou partes de fragmentos.

    exemplo:
    ```json
    https://widgets.example.com/files/v1/documents/1111
    ```

    exemplo inválido:
    ```json
    https://social.example.com/social/v1/@me/documentlibrary?filterBy=id&amp;filterValue=1111
    ```

    Ambos os URLs acima podem retornar a mesma representação de recurso, no entanto, o primeiro representa um URL canônico válido, enquanto o segundo não.

3. A URL canônica de um recurso DEVE ser globalmente única no espaço e no tempo, de modo que nunca represente um recurso diferente daquele para o qual foi originalmente definida.	

4. A URL canônica de um recurso DEVE ser imutável, de modo que possa ser usada para acessar seu recurso associado a qualquer momento enquanto esse recurso existir.	

5. Um serviço NÃO DEVE aplicar qualquer interpretação semântica da porção de domínio da URL ou Host HTTP: cabeçalho ao atender uma solicitação. Ou seja, a parte do host e do domínio de uma solicitação DEVE ser considerada opaca.	

6. Para o propósito destas diretrizes, definimos dois tipos de endpoints HTTP. Os endpoints da interface do usuário são URLs que fornecem acesso a recursos destinados ao uso direto por um navegador da Web (por exemplo, HTML, gráficos, JavaScript etc.). Os endpoints da API são URLs que fornecem acesso a recursos destinados ao uso por clientes de aplicativos e outros serviços (por exemplo, dados estruturados na forma de documentos JSON, vCards, etc.). Além disso, definimos domínio personalizado como qualquer FQDN contendo um identificador específico do cliente (por exemplo, api.company.example.com). Dadas essas definições, o seguinte se aplica ao uso de domínios personalizados em implantações de serviços baseados em nuvem:
    - Uma implantação de serviço PODE oferecer suporte a domínios personalizados na parte do domínio dos pontos de extremidade da interface do usuário.
    - Uma implantação de serviço PODE oferecer suporte a domínios personalizados na parte do domínio dos endpoints da API se e somente se a implantação do serviço já estiver em produção e em uso por clientes fora da Cisco.
    - Em todos os outros casos, uma implantação de serviço NÃO DEVE oferecer suporte a domínios personalizados.

7. Ao representar uma matriz de valores por meio de parâmetros de consulta de URL, um serviço DEVE oferecer suporte à representação discreta de cada valor da matriz como um par nome/valor separado na parte de consulta da URL.

    exemplo:
    ```json
    https://widgets.example.com/files/v1/documents/id=1111&id=2222&id=3333
    ```

8. Ao representar uma matriz de valores por meio de parâmetros de consulta de URL, um serviço PODE, além do formato descrito em 3.4.1.7, também oferecer suporte a uma única string delimitada por vírgula concatenação dos valores da matriz como um único par nome/valor na parte de consulta do URL. Isso é fornecido se os valores válidos do atributo não permitirem vírgulas incorporadas.	

    exemplo:
    ```json
    https://widgets.example.com/files/v1/documents/id=1111,2222,3333
    ```

9. Um serviço PODE suportar o uso de um token **@me** em qualquer URL e interpretá-lo como o ID do usuário correspondente ao originador da solicitação autenticada.

    modelo:
    ```json
    https://{domain}/{service}/{version}/{...}/@me/{...}
    ```

    exemplo:
    ```json
    https://widgets.example.com/files/v3/documents/@me/public
    ```

    pode ser interpretado como:
    ```json
    https://widgets.example.com/files/v3/documents/b569fe/public
    ```

onde b569fe é o userId do originador da solicitação.

10. Na ausência de um ID de usuário explícito ou token **@me** na URL, um serviço NÃO DEVE presumir que um endpoint se destina a se referir a um recurso relacionado ao originador da solicitação autenticada.	

    exemplo:
    ```json
    https://widgets.example.com/files/v3/documents
    ```

O acima não pode ser assumido como representando documentos de propriedade do originador da solicitação.

11. Um serviço NÃO DEVE exigir o uso de um token **@me** em qualquer URL.

## Cabeçalhos HTTP

1. Os serviços DEVEM oferecer suporte ao cabeçalho ETag em qualquer resposta HTTP em que seja razoável para clientes ou proxies armazenar em cache a representação de recurso associada. Nos casos em que a ETag é suportada, esses recursos DEVEM também oferecer suporte aos cabeçalhos If-Match e If-None-Match. Quando o cache não for apropriado, os serviços DEVEM incluir um cabeçalho Cache-Control (por exemplo, max-age=0, no-cache, no-store, must-revalidate) e NÃO DEVEM incluir um cabeçalho ETag.	 

2. Os serviços DEVEM oferecer suporte a codificações de conteúdo de compactação HTTP padrão, conforme descrito na seção 3.5 da especificação HTTP.	 

3. Se uma solicitação incluir um cabeçalho HTTP Accept, o serviço DEVE retornar uma representação de recurso correspondente a um tipo apresentado nesse cabeçalho ou retornar um código de erro apropriado. As exceções incluem a seguinte lista de formatos, que DEVEM ser considerados como aceitação pelo cliente de respostas com representações de recursos no formato JSON.

    ```json
    application/x-www-form-urlencoded
    texto/simples
    ```

4. A ausência de um cabeçalho Accept em uma solicitação DEVE ser considerada como aceitação pelo cliente de respostas com representações de recursos no formato JSON.	 

5. Ao responder a uma solicitação com um código de erro, o serviço PODE retornar a carga de resposta formatada em JSON descrita na seção 3.9, independentemente da presença ou conteúdo de um cabeçalho Accept na solicitação original.	 

6. Se o último segmento do caminho de uma URL de solicitação contiver um "." (ponto) o serviço DEVE considerar a parte da URL após esse ponto como uma "extensão de formato" e a parte anterior da URL como identificação do recurso real a ser operado.	 

7. Uma solicitação feita em um URL com uma extensão de formato DEVE ser tratada como se o formato correspondente fosse fornecido como um cabeçalho Accept: na solicitação (baseado em Apache MIME para mapeamentos de extensão de arquivo [MIME2EXT9](## Referências). Se a solicitação contiver um cabeçalho Accept: explícito, o O cabeçalho Accept explícito DEVE ser desconsiderado em favor da extensão do formato.	 

8. Uma solicitação feita em uma URL com uma extensão de formato e incluindo conteúdo de entidade DEVE resultar em uma resposta de erro se o cabeçalho Content-Type: da solicitação não corresponder ao formato indicado pela extensão de formato (baseado em Apache MIME para mapeamentos de extensão de arquivo MIME2EXT . As exceções incluem casos em que o cabeçalho Content-Typeestá ausente na solicitação ou indica um ou mais dos seguintes formatos:

    ```json
    application/x-www-form-urlencoded
    texto/simples
    ```
    Nesses casos de exceção, o cabeçalho Content-Type explícito DEVE ser desconsiderado em favor da extensão do formato.

9. Um serviço DEVE oferecer suporte a fluxos de solicitação CORS simples e simulados. Os serviços DEVEM retornar "**" como cabeçalho Access-Control-Allow-Origin, a menos que a solicitação seja acompanhada por um cabeçalho Origin, caso em que o serviço DEVE retornar um cabeçalho Access-Control-Allow-Origin com um valor igual a esse do cabeçalho Origin recebido. Os serviços NÃO DEVEM retornar um cabeçalho access-control-allow-credentials em nenhuma resposta HTTP.

10. Um serviço DEVE incluir um cabeçalho TrackingID exclusivo em cada solicitação da API REST que envia para outro serviço. O valor do TrackingID DEVE incluir um sendertype e uma parte uuid, e PODE incluir uma ou mais partes nvpair, e PODE incluir uma ou mais partes de sequência. O formato do valor TrackingID DEVE ser estruturado conforme definido no seguinte modelo ABNF:

    modelo:
    ```json
    trackingid = sendertype uuid *nvpair *sequence
    tipo de remetente = 1*ALFA
    uuid = "_" 8HEXDIG "-" 4HEXDIG "-" 4HEXDIG "-" 4HEXDIG "-" 12HEXDIG
    nvpair = "_" nome ":" valor
    sequência = "_" 1*DIGITO
    nome = 1*ALFA
    valor = 1*ALFA
    ```
    
    exemplos:
    ```json
    WX2_550e8400-e29b-41d4-a716-446655440000
    WX2_550e8400-e29b-41d4-a716-446655440000_0
    WX2_550e8400-e29b-41d4-a716-446655440000_1_1_3
    WX2_550e8400-e29b-41d4-a716-446655440000_locus:1234
    WX2_550e8400-e29b-41d4-a716-446655440000_locus:1234_0_1
    WX2_550e8400-e29b-41d4-a716-446655440000_locus:1234_calliope:5678
    WX2_550e8400-e29b-41d4-a716-446655440000_locus:1234_calliope:5678_1_2
    ```

## Referências

- [RWS] RESTful Web Services http://shop.oreilly.com/product/9780596529260.do.
- [ROS] RESTful Objects Specification http://restfulobjects.org.
- [DISC1] Google APIs Discovery Service https://developers.google.com/discovery/v1/using#discovery-doc-apiproperties.
- [DISC2] Aggregated Service Discovery http://tools.ietf.org/html/draft-daboo-aggregated-service-discovery-01.
- [MIME2EXT] Apache MIME to File Extension Mapping http://svn.apache.org/repos/asf/httpd/httpd/trunk/docs/conf/mime.types.
- [JCR] A Language for Rules Describing JSON Content http://tools.ietf.org/html/draft-newton-json-content-rules-00.
- [JP] JSON Patch http://tools.ietf.org/html/rfc6902.
- [URI] URI Template http://tools.ietf.org/html/rfc6570.
- [HTTP] Hypertext Transfer Protocol – HTTP/1.1 http://www.ietf.org/rfc/rfc2616.txt.
- [OAUTH4] The OAuth 2.0 Authorization Framework: Bearer Token Usage http://tools.ietf.org/html/rfc6750.
- [RFC3986] RFC-3986 http://www.ietf.org/rfc/rfc3986.txt.
- [CORS] Cross-Origin Resource Sharing http://www.w3.org/TR/cors/.
- [RFC3339] Date and Time on the Internet: Timestamps http://www.ietf.org/rfc/rfc3339.txt.
- [SCIMSCHEMA] SCIM Schema http://tools.ietf.org/html/draft-ietf-scim-core-schema.
- [JSONPATH] JSONPath http://goessner.net/articles/JsonPath/.