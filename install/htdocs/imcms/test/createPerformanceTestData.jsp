<%@ page import="com.imcode.imcms.api.ContentManagementSystem,
                 com.imcode.imcms.api.RequestConstants,
                 com.imcode.imcms.api.DocumentService,
                 com.imcode.imcms.api.TextDocument"%>

<%
    String parentIdParamName = "parentId";
    String parentIdStr = request.getParameter(parentIdParamName);
    if( null != parentIdStr ) {
        int parentId = Integer.parseInt(parentIdStr);

        ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
        DocumentService documentService = imcmsSystem.getDocumentService() ;

        TextDocument helloWorldTextDocument = documentService.createNewTextDocument( parentId, 1 );
        helloWorldTextDocument.setHeadline("\'Hello World\' document");
        documentService.saveChanges(helloWorldTextDocument);

        TextDocument textDocumentWithSomeTexts = documentService.createNewTextDocument(parentId,1);
        textDocumentWithSomeTexts.setHeadline("Text document with some texts");
        textDocumentWithSomeTexts.setPlainTextField(1,"Einstein: A man's ethical behavior should be based effectually on sympathy, education, and social ties; no religious basis is necessary. Man would indeed be in a poor way if he had to be restrained by fear of punishment and hope of reward after death." );
        textDocumentWithSomeTexts.setPlainTextField(2,"Einstein: Everything should be made as simple as possible, but not simpler." );
        documentService.saveChanges(textDocumentWithSomeTexts);

        TextDocument textDocumentWithLargerText = documentService.createNewTextDocument(parentId,1);
        textDocumentWithLargerText.setHeadline("A text document with larger text");
        textDocumentWithLargerText.setHtmlTextField(1,largeHtmlText );
        documentService.saveChanges(textDocumentWithLargerText);

        TextDocument textDocumentWithTreeIncludes = documentService.createNewTextDocument( parentId, 1 );
        textDocumentWithTreeIncludes.setHeadline("Text document with three includes");
        textDocumentWithTreeIncludes.setInclude(1, helloWorldTextDocument );
        textDocumentWithTreeIncludes.setInclude(2, textDocumentWithSomeTexts );
        textDocumentWithTreeIncludes.setInclude(3, textDocumentWithLargerText );
        documentService.saveChanges(textDocumentWithTreeIncludes);
        %>
            Created the following documents:<br>
            'HelloWorld' document id = <%=helloWorldTextDocument.getId()%><br>
            Text document with some texts id = <%=textDocumentWithSomeTexts.getId()%><br>
            Text document with a larger text = <%=textDocumentWithLargerText.getId()%><br>
            Text document with tree includes id = <%=textDocumentWithTreeIncludes.getId()%><br>
         <%
    } else {
        %>
        <form action="createPerformanceTestData.jsp" method="get">
            <input name="parentId" type="text" value="1001">
            <input name="" type="submit">
        </form>
        <%
    }
%>

<%!
private final static String largeHtmlText = "<p align=justify>\n" +
"Around 1886 <b>Albert Einstein</b> began his school career in Munich. As well as his violin lessons, which he had from age six to age thirteen, he also had religious education at home where he was taught Judaism. Two years later he entered the Luitpold <a href=\"javascript:win1('../Glossary/gymnasium',350,200)\" onmouseover=\"window.status='Click for glossary entry';return true\"><font color=\"green\" style=\"text-decoration:none\">Gymnasium</font></a> and after this his religious education was given at school. He studied mathematics, in particular the calculus, beginning around 1891.\n" +
"\n" +
"<p align=justify>\n" +
"In 1894 Einstein's family moved to Milan but Einstein remained in Munich. In 1895 Einstein failed an examination that would have allowed him to study for a diploma as an electrical engineer at the Eidgen&ouml;ssische Technische Hochschule in Zurich. Einstein renounced German citizenship in 1896 and was to be stateless for a number of years. He did not even apply for Swiss citizenship until 1899, citizenship being granted in 1901. \n" +
"<p align=justify>\n" +
"Following the failing of the entrance exam to the ETH, Einstein attended secondary school at Aarau planning to use this route to enter the ETH in Zurich. While at Aarau he wrote an essay (for which was only given a little above half marks!) in which he wrote of his plans for the future, see [13]:-\n" +
"<blockquote><p align=justify>\n" +
"<i>If I were to have the good fortune to pass my examinations, I would go to Zurich. I would stay there for four years in order to study mathematics and physics. I imagine myself becoming a teacher in those branches of the natural sciences, choosing the theoretical part of them. Here are the reasons which lead me to this plan. Above all, it is my disposition for abstract and mathematical thought, and my lack of imagination and practical ability.</i>\n" +
"</blockquote><p align=justify>\n" +
"Indeed Einstein succeeded with his plan graduating in 1900 as a teacher of mathematics and physics. One of his friends at ETH was Marcel <a href=\"Grossmann.html\">Grossmann</a> who was in the same class as Einstein. Einstein tried to obtain a post, writing to <a href=\"Hurwitz.html\">Hurwitz</a> who held out some hope of a position but nothing came of it. Three of Einstein's fellow students, including <a href=\"Grossmann.html\">Grossmann</a>, were appointed assistants at ETH in Zurich but clearly Einstein had not impressed enough and still in 1901 he was writing round universities in the hope of obtaining a job, but without success. \n" +
"\n" +
"<p align=justify>\n" +
"He did manage to avoid Swiss military service on the grounds that he had flat feet and varicose veins. By mid 1901 he had a temporary job as a teacher, teaching mathematics at the Technical High School in Winterthur. Around this time he wrote:-\n" +
"<blockquote><p align=justify>\n" +
"<i>I have given up the ambition to get to a university ...</i>\n" +
"</blockquote><p align=justify>\n" +
"Another temporary position teaching in a private school in Schaffhausen followed. Then <a href=\"Grossmann.html\">Grossmann</a>'s father tried to help Einstein get a job by recommending him to the director of the patent office in Bern. Einstein was appointed as a technical expert third class.\n" +
"<p align=justify>\n" +
"Einstein worked in this patent office from 1902 to 1909, holding a temporary post when he was first appointed, but by 1904 the position was made permanent and in 1906 he was promoted to technical expert second class. While in the Bern patent office he completed an astonishing range of theoretical physics publications, written in his spare time without the benefit of close contact with scientific literature or colleagues. \n" +
"<p align=justify>\n" +
"Einstein earned a doctorate from the University of Zurich in 1905 for a thesis <i>On a new determination of molecular dimensions.</i> He dedicated the thesis to <a href=\"Grossmann.html\">Grossmann</a>. \n" +
"\n" +
"<p align=justify>\n" +
"In the first of three papers, all written in 1905, Einstein examined the phenomenon discovered by Max <a href=\"Planck.html\">Planck</a>, according to which electromagnetic energy seemed to be emitted from radiating objects in discrete quantities. The energy of these quanta was directly proportional to the frequency of the radiation. This seemed to contradict classical electromagnetic theory, based on <a href=\"Maxwell.html\">Maxwell</a>'s equations and the laws of thermodynamics which assumed that electromagnetic energy consisted of waves which could contain any small amount of energy. Einstein used <a href=\"Planck.html\">Planck</a>'s quantum hypothesis to describe the electromagnetic radiation of light. \n" +
"<p align=justify>\n" +
"Einstein's second 1905 paper proposed what is today called the special theory of relativity. He based his new theory on a reinterpretation of the classical principle of relativity, namely that the laws of physics had to have the same form in any frame of reference. As a second fundamental hypothesis, Einstein assumed that the speed of light remained constant in all frames of reference, as required by <a href=\"Maxwell.html\">Maxwell</a>'s theory. \n" +
"<p align=justify>\n" +
"Later in 1905 Einstein showed how mass and energy were equivalent. Einstein was not the first to propose all the components of special theory of relativity. His contribution is unifying important parts of classical mechanics and <a href=\"Maxwell.html\">Maxwell</a>'s electrodynamics.\n" +
"<p align=justify>\n" +
"\n" +
"The third of Einstein's papers of 1905 concerned <a href=\"javascript:win1('../Glossary/statistical_mechanics',350,200)\" onmouseover=\"window.status='Click for glossary entry';return true\"><font color=\"green\" style=\"text-decoration:none\">statistical mechanics</font></a>, a field of that had been studied by Ludwig <a href=\"Boltzmann.html\">Boltzmann</a> and Josiah <a href=\"Gibbs.html\">Gibbs</a>. \n" +
"<p align=justify>\n" +
"After 1905 Einstein continued working in the areas described above. He made important contributions to <a href=\"javascript:win1('../Glossary/quantum_mechanics',350,200)\" onmouseover=\"window.status='Click for glossary entry';return true\"><font color=\"green\" style=\"text-decoration:none\">quantum theory</font></a>, but he sought to extend the special theory of relativity to phenomena involving acceleration. The key appeared in 1907 with the principle of equivalence, in which gravitational acceleration was held to be indistinguishable from acceleration caused by mechanical forces. Gravitational mass was therefore identical with inertial mass.\n" +
"<p align=justify>\n" +
"In 1908 Einstein became a lecturer at the University of Bern after submitting his <a href=\"javascript:win1('../Glossary/habilitation',350,200)\" onmouseover=\"window.status='Click for glossary entry';return true\"><font color=\"green\" style=\"text-decoration:none\">Habilitation</font></a> thesis <i>Consequences for the constitution of radiation following from the energy distribution law of black bodies.</i> The following year he become professor of physics at the University of Zurich, having resigned his lectureship at Bern and his job in the patent office in Bern.\n" +
"\n" +
"<p align=justify>\n" +
"By 1909 Einstein was recognised as a leading scientific thinker and in that year he resigned from the patent office. He was appointed a full professor at the Karl-Ferdinand University in Prague in 1911. In fact 1911 was a very significant year for Einstein since he was able to make preliminary predictions about how a ray of light from a distant star, passing near the Sun, would appear to be bent slightly, in the direction of the Sun. This would be highly significant as it would lead to the first experimental evidence in favour of Einstein's theory.\n" +
"<p align=justify>\n" +
"About 1912, Einstein began a new phase of his gravitational research, with the help of his mathematician friend Marcel <a href=\"Grossmann.html\">Grossmann</a>, by expressing his work in terms of the <a href=\"javascript:win1('../Glossary/tensor',350,200)\" onmouseover=\"window.status='Click for glossary entry';return true\"><font color=\"green\" style=\"text-decoration:none\">tensor</font></a> calculus of Tullio <a href=\"Levi-Civita.html\">Levi-Civita</a> and Gregorio <a href=\"Ricci-Curbastro.html\">Ricci-Curbastro</a>. Einstein called his new work the general theory of relativity. He moved from Prague to Zurich in 1912 to take up a chair at the Eidgen&ouml;ssische Technische Hochschule in Zurich.\n" +
"<p align=justify>\n" +
"Einstein returned to Germany in 1914 but did not reapply for German citizenship. What he accepted was an impressive offer. It was a research position in the Prussian Academy of Sciences together with a chair (but no teaching duties) at the University of Berlin. He was also offered the directorship of the Kaiser Wilhelm Institute of Physics in Berlin which was about to be established. \n" +
"\n" +
"<p align=justify>\n" +
"After a number of false starts Einstein published, late in 1915, the definitive version of general theory. Just before publishing this work he lectured on general relativity at G&ouml;ttingen and he wrote:-\n" +
"<blockquote><p align=justify>\n" +
"<i>To my great joy, I completely succeeded in convincing </i><a href=\"Hilbert.html\">Hilbert</a><i> and </i><a href=\"Klein.html\">Klein</a>.\n" +
"</blockquote><p align=justify>\n" +
"In fact <a href=\"Hilbert.html\">Hilbert</a> submitted for publication, a week before Einstein completed his work, a paper which contains the correct field equations of general relativity.\n" +
"<p align=justify>\n" +
"When British eclipse expeditions in 1919 confirmed his predictions, Einstein was idolised by the popular press. The London <i>Times</i> ran the headline on 7 November 1919:-\n" +
"\n" +
"<blockquote><p align=justify>\n" +
"<i>Revolution in science - New theory of the Universe - Newtonian ideas overthrown.</i>\n" +
"</blockquote><p align=justify>\n" +
"In 1920 Einstein's lectures in Berlin were disrupted by demonstrations which, although officially denied, were almost certainly anti-Jewish. Certainly there were strong feelings expressed against his works during this period which Einstein replied to in the press quoting <a href=\"Lorentz.html\">Lorentz</a>, <a href=\"Planck.html\">Planck</a> and <a href=\"Eddington.html\">Eddington</a> as supporting his theories and stating that certain Germans would have attacked them if he had been:-\n" +
"<blockquote><p align=justify>\n" +
"<i>... a German national with or without swastika instead of a Jew with liberal international convictions...</i>\n" +
"</blockquote><p align=justify>\n" +
"\n" +
"During 1921 Einstein made his first visit to the United States. His main reason was to raise funds for the planned Hebrew University of Jerusalem. However he received the Barnard Medal during his visit and lectured several times on relativity. He is reported to have commented to the chairman at the lecture he gave in a large hall at Princeton which was overflowing with people:-\n" +
"<blockquote><p align=justify>\n" +
"<i>I never realised that so many Americans were interested in tensor analysis.</i>\n" +
"</blockquote><p align=justify>\n" +
"Einstein received the Nobel Prize in 1921 but not for relativity rather for his 1905 work on the photoelectric effect. In fact he was not present in December 1922 to receive the prize being on a voyage to Japan. Around this time he made many international visits. He had visited Paris earlier in 1922 and during 1923 he visited Palestine. After making his last major scientific discovery on the association of waves with matter in 1924 he made further visits in 1925, this time to South America.\n" +
"<p align=justify>\n" +
"Among further honours which Einstein received were the Copley Medal of the Royal Society in 1925 and the Gold Medal of the Royal Astronomical Society in 1926.\n" +
"<p align=justify>\n" +
"<a href=\"Bohr_Niels.html\">Niels Bohr</a> and Einstein were to carry on a debate on quantum theory which began at the Solvay Conference in 1927. <a href=\"Planck.html\">Planck</a>, <a href=\"Bohr_Niels.html\">Niels Bohr</a>, de <a href=\"Broglie.html\">Broglie</a>, <a href=\"Heisenberg.html\">Heisenberg</a>, <a href=\"Schrodinger.html\">Schr&ouml;dinger</a> and <a href=\"Dirac.html\">Dirac</a> were at this conference, in addition to Einstein. Einstein had declined to give a paper at the conference and:-\n" +
"\n" +
"<blockquote><p align=justify>\n" +
"<i>... said hardly anything beyond presenting a very simple objection to the probability interpretation </i>....<i> Then he fell back into silence ...</i>\n" +
"</blockquote><p align=justify>\n" +
"Indeed Einstein's life had been hectic and he was to pay the price in 1928 with a physical collapse brought on through overwork. However he made a full recovery despite having to take things easy throughout 1928. \n" +
"<p align=justify>\n" +
"By 1930 he was making international visits again, back to the United States. A third visit to the United States in 1932 was followed by the offer of a post at Princeton. The idea was that Einstein would spend seven months a year in Berlin, five months at Princeton. Einstein accepted and left Germany in December 1932 for the United States. The following month the Nazis came to power in Germany and Einstein was never to return there.\n" +
"<p align=justify>\n" +
"During 1933 Einstein travelled in Europe visiting Oxford, Glasgow, Brussels and Zurich. Offers of academic posts which he had found it so hard to get in 1901, were plentiful. He received offers from Jerusalem, Leiden, Oxford, Madrid and Paris.\n" +
"<p align=justify>\n" +
"What was intended only as a visit became a permanent arrangement by 1935 when he applied and was granted permanent residency in the United States. At Princeton his work attempted to unify the laws of physics. However he was attempting problems of great depth and he wrote:-\n" +
"<blockquote><p align=justify>\n" +
"<i>I have locked myself into quite hopeless scientific problems - the more so since, as an elderly man, I have remained estranged from the society here...</i>\n" +
"\n" +
"</blockquote><p align=justify>\n" +
"In 1940 Einstein became a citizen of the United States, but chose to retain his Swiss citizenship. He made many contributions to peace during his life. In 1944 he made a contribution to the war effort by hand writing his 1905 paper on special relativity and putting it up for auction. It raised six million dollars, the manuscript today being in the Library of Congress.\n" +
"<p align=justify>\n" +
"By 1949 Einstein was unwell. A spell in hospital helped him recover but he began to prepare for death by drawing up his will in 1950. He left his scientific papers to the Hebrew University in Jerusalem, a university which he had raised funds for on his first visit to the USA, served as a governor of the university from 1925 to 1928 but he had turned down the offer of a post in 1933 as he was very critical of its administration.\n" +
"<p align=justify>\n" +
"One more major event was to take place in his life. After the death of the first president of Israel in 1952, the Israeli government decided to offer the post of second president to Einstein. He refused but found the offer an embarrassment since it was hard for him to refuse without causing offence.\n" +
"<p align=justify>\n" +
"One week before his death Einstein signed his last letter. It was a letter to Bertrand <a href=\"Russell.html\">Russell</a> in which he agreed that his name should go on a manifesto urging all nations to give up nuclear weapons. It is fitting that one of his last acts was to argue, as he had done all his life, for international peace.\n" +
"<p align=justify>\n" +
"Einstein was cremated at Trenton, New Jersey at 4 pm on 18 April 1955 (the day of his death). His ashes were scattered at an undisclosed place.<br>\n" +
"<p><font color=purple><b>Article by:</b> <i>J J O'Connor</i> and <i>E F Robertson</i></font><p>";
%>

