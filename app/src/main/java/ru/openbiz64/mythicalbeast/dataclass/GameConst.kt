package ru.openbiz64.mythicalbeast.dataclass

object GameConst {

    const val TYPE = "type"
    const val CAPTION = "caption"
    const val LEVEL = "level"
    const val GOAL = "goal"
    const val MISS = "miss"
    const val DATA_MATCHDAY = "matchday_json"
    const val TYPE_MATCHDAY = "matchday"
    const val TYPE_CUP = "cup"
    const val TYPE_CLUB = "club"
    const val TYPE_LOGO = "logo"
    const val TYPE_STADIUM = "stadium"


    const val STATISTIC = "statistic"
    const val CORRECT = "correct"
    const val WRONG = "wrong"

    const val goal_logo_rpl = 5
    const val mis_logo_rpl = 1
    const val goal_logo_fnl = 4
    const val mis_logo_fnl = 2
    const val goal_logo_pfla = 3
    const val mis_logo_pfla = 3
    const val goal_logo_pflb = 3
    const val mis_logo_pflb = 3

    const val for_stadium_rpl = 7
    const val for_stadium_fnl = 6
    const val for_stadium_pfla = 5
    const val for_stadium_pflb = 4

    const val for_cup_128 = 7
    const val for_cup_64 = 6
    const val for_cup_32 = 6
    const val for_cup_16 = 5
    const val for_cup_8 = 5
    const val for_cup_4 = 4
    const val for_cup_2 = 4
    const val for_cup_F = 3


    const val question_for_cupgame_count = 20
    const val question_for_champgame_count = 15 //25
    const val min_for_next_game = 16

    const val for_fn_1 = 25
    const val for_fn_2 = 22
    const val for_fn_3 = 19
    const val for_fn_4 = 16
    const val for_fn_5 = 13
    const val for_fn_6 = 10
    const val for_fn_7 = 7

    const val min_forRace_1 = 5
    const val min_forRace_2 = 4
    const val min_forRace_3 = 3

    const val saveContaner = "GameDataShared"


    const val GN_MatchDay = "МАТЧ ТУРА"
    const val GN_Cup = "КУБОК"
    const val GN_Champ = "Чемпионат"
    const val GN_Club= "КЛУБЫ"
    const val GN_Stadium = "СТАДИОНЫ"
    const val GN_Logo = "ЭМБЛЕМЫ"

    const val GD_MatchDay = "Загрузка..."
    const val GD_Cup = "Пройди викторину от 1/128 до Финала"
    const val GD_Champ = "Это не просто викторина, а настоящий чемпионат!"
    const val GD_Club = "Вопросы по истории и игрокам клубов РПЛ"
    const val GD_Stadium = "Вопросы по стадионам РПЛ и ФНЛ"
    const val GD_Logo = "Вопросы по эмблемам футбольных клубов России"

    const val GP_MatchDay = "gamelogo_matchday"
    const val GP_Cup = "gamelogo_cup"
    const val GP_Champ = "gamelogo_champ"
    const val GP_Club = "gamelogo_club"
    const val GP_Stadium = "gamelogo_stadium"
    const val GP_Logo = "gamelogo_logo"


    const val SN_TabRPL = "ТУРНИРНАЯ ТАБЛИЦА"
    const val SN_TabFNL = "ТУРНИРНАЯ ТАБЛИЦА"
    const val SN_CupRussia = "КУБОК РОССИИ"
    const val SN_TabPlayerRPL= "СТАТИСТИКА РПЛ"
    const val SN_TabPlayerFNL = "СТАТИСТИКА ФНЛ"
    const val SN_TabStage = "КАЛЕНДАРЬ РПЛ"

    const val SD_TabRPL = "tabrpl"
    const val SD_TabFNL = "tabfnl"
    const val SD_CupRussia = "cuprus"
    const val SD_TabPlayerRPL= "statrpl"
    const val SD_TabPlayerFNL = "statfnl"
    const val SD_TabStage = "calendar"

    const val SU_TabRPL       = "https://amaranth64.github.io/RPL_V2/html/tableRPL.html"
    const val SU_TabFNL       = "https://amaranth64.github.io/RPL_V2/html/tableFNL.html"
    const val SU_CupRussia    = "https://amaranth64.github.io/RPL_V2/html/cupRussia.html"
    const val SU_TabPlayerRPL = "https://amaranth64.github.io/RPL_V2/html/statisticRPL.html"
    const val SU_TabPlayerFNL = "https://amaranth64.github.io/RPL_V2/html/statisticFNL.html"
    const val SU_TabStage     = "https://amaranth64.github.io/RPL_V2/html/calendarRPL.html"

    const val IN_ClubRPL_ZenitS = "Зенит СПб"
    const val IN_ClubRPL_Krasno = "Краснодар"
    const val IN_ClubRPL_DinamM = "Динамо Мск"
    const val IN_ClubRPL_LokomM = "Локомотив М"
    const val IN_ClubRPL_SpartM = "Спартак М"
    const val IN_ClubRPL_CSKA_M = "ЦСКА"
    const val IN_ClubRPL_Rostov = "Ростов"
    const val IN_ClubRPL_RubinK = "Рубин"
    const val IN_ClubRPL_KrSove = "Крылья Советов"
    const val IN_ClubRPL_Akhmat = "Ахмат"
    const val IN_ClubRPL_FakelV = "Факел"
    const val IN_ClubRPL_Orenbu = "Оренбург"
    const val IN_ClubRPL_Khimki = "Химки"
    const val IN_ClubRPL_DimamX = "Динамо Мах"
    const val IN_ClubRPL_PariNN = "Пари НН"
    const val IN_ClubRPL_AkronT = "Акрон"

    const val IP_ClubRPL_ZenitS = "logo_zenit"
    const val IP_ClubRPL_Krasno = "logo_krasnodar"
    const val IP_ClubRPL_DinamM = "logo_dinamo"
    const val IP_ClubRPL_LokomM = "logo_lokomotiv"
    const val IP_ClubRPL_SpartM = "logo_spartak"
    const val IP_ClubRPL_CSKA_M = "logo_cska"
    const val IP_ClubRPL_Rostov = "logo_rostov"
    const val IP_ClubRPL_RubinK = "logo_rubin"
    const val IP_ClubRPL_KrSove = "logo_ks"
    const val IP_ClubRPL_Akhmat = "logo_akhmat"
    const val IP_ClubRPL_FakelV = "logo_fakel"
    const val IP_ClubRPL_Orenbu = "logo_orenburg"
    const val IP_ClubRPL_Khimki = "logo_khimki"
    const val IP_ClubRPL_DimamX = "logo_dinamo_mah"
    const val IP_ClubRPL_PariNN = "logo_pari"
    const val IP_ClubRPL_AkronT = "logo_akron"

    const val IU_ClubRPL_ZenitS = "https://ru.wikipedia.org/wiki/Зенит_(футбольный_клуб,_Санкт-Петербург)"
    const val IU_ClubRPL_Krasno = "https://ru.wikipedia.org/wiki/Краснодар_(футбольный_клуб)"
    const val IU_ClubRPL_DinamM = "https://ru.wikipedia.org/wiki/Динамо_(футбольный_клуб,_Москва)"
    const val IU_ClubRPL_LokomM = "https://ru.wikipedia.org/wiki/Локомотив_(футбольный_клуб,_Москва)"
    const val IU_ClubRPL_SpartM = "https://ru.wikipedia.org/wiki/Спартак_(футбольный_клуб,_Москва)"
    const val IU_ClubRPL_CSKA_M = "https://ru.wikipedia.org/wiki/ЦСКА_(футбольный_клуб,_Москва)"
    const val IU_ClubRPL_Rostov = "https://ru.wikipedia.org/wiki/Ростов_(футбольный_клуб)"
    const val IU_ClubRPL_RubinK = "https://ru.wikipedia.org/wiki/Рубин_(футбольный_клуб)"
    const val IU_ClubRPL_KrSove = "https://ru.wikipedia.org/wiki/Крылья_Советов_(футбольный_клуб,_Самара)"
    const val IU_ClubRPL_Akhmat = "https://ru.wikipedia.org/wiki/Ахмат_(футбольный_клуб)"
    const val IU_ClubRPL_FakelV = "https://ru.wikipedia.org/wiki/Факел_(футбольный_клуб,_Воронеж)"
    const val IU_ClubRPL_Orenbu = "https://ru.wikipedia.org/wiki/Оренбург_(футбольный_клуб)"
    const val IU_ClubRPL_Khimki = "https://ru.wikipedia.org/wiki/Химки_(футбольный_клуб)"
    const val IU_ClubRPL_DimamX = "https://ru.wikipedia.org/wiki/Динамо_(футбольный_клуб,_Махачкала)"
    const val IU_ClubRPL_PariNN = "https://ru.wikipedia.org/wiki/Пари_Нижний_Новгород_(футбольный_клуб)"
    const val IU_ClubRPL_AkronT = "https://ru.wikipedia.org/wiki/Акрон_(футбольный_клуб)"

    const val IN_ClubFNL_Torp  = "Торпедо Москва"
    const val IN_ClubFNL_Neft  = "Нефтехимик"
    const val IN_ClubFNL_Ural  = "Урал"
    const val IN_ClubFNL_Enis  = "Енисей"
    const val IN_ClubFNL_Arsl  = "Арсенал Тула"
    const val IN_ClubFNL_Rotr  = "Ротор"
    const val IN_ClubFNL_Sokl  = "Сокол Саратов"
    const val IN_ClubFNL_Chay  = "Чайка"
    const val IN_ClubFNL_Balt  = "Балтика"
    const val IN_ClubFNL_Rodn  = "Родина"
    const val IN_ClubFNL_Alan  = "Алания"
    const val IN_ClubFNL_Ufau  = "Уфа"
    const val IN_ClubFNL_Kamz  = "КАМАЗ"
    const val IN_ClubFNL_Cher  = "Черноморец"
    const val IN_ClubFNL_Shin  = "Шинник"
    const val IN_ClubFNL_Soci  = "Сочи"
    const val IN_ClubFNL_Tumn  = "Тюмень"
    const val IN_ClubFNL_Skah  = "СКА Хабаровск"

    const val IP_ClubFNL_Torp  = "logo_torpedo"
    const val IP_ClubFNL_Neft  = "logo_neftehimik"
    const val IP_ClubFNL_Ural  = "logo_ural"
    const val IP_ClubFNL_Enis  = "logo_enisey"
    const val IP_ClubFNL_Arsl  = "logo_arsenal"
    const val IP_ClubFNL_Rotr  = "logo_rotor"
    const val IP_ClubFNL_Sokl  = "logo_sokol"
    const val IP_ClubFNL_Chay  = "logo_chayka_pes"
    const val IP_ClubFNL_Balt  = "logo_baltika"
    const val IP_ClubFNL_Rodn  = "logo_rodina"
    const val IP_ClubFNL_Alan  = "logo_alania"
    const val IP_ClubFNL_Ufau  = "logo_ufa"
    const val IP_ClubFNL_Kamz  = "logo_kamaz"
    const val IP_ClubFNL_Cher  = "logo_chernomorez"
    const val IP_ClubFNL_Shin  = "logo_shinnik"
    const val IP_ClubFNL_Soci  = "logo_sochi"
    const val IP_ClubFNL_Tumn  = "logo_tumen"
    const val IP_ClubFNL_Skah  = "logo_ska_hab"

    const val IU_ClubFNL_Torp  = "https://ru.wikipedia.org/wiki/Торпедо_(футбольный_клуб,_Москва)"
    const val IU_ClubFNL_Neft  = "https://ru.wikipedia.org/wiki/Нефтехимик_(футбольный_клуб,_Нижнекамск)"
    const val IU_ClubFNL_Ural  = "https://ru.wikipedia.org/wiki/Урал_(футбольный_клуб)"
    const val IU_ClubFNL_Enis  = "https://ru.wikipedia.org/wiki/Енисей_(футбольный_клуб)"
    const val IU_ClubFNL_Arsl  = "https://ru.wikipedia.org/wiki/Арсенал_(футбольный_клуб,_Тула)"
    const val IU_ClubFNL_Rotr  = "https://ru.wikipedia.org/wiki/Ротор_(футбольный_клуб)"
    const val IU_ClubFNL_Sokl  = "https://ru.wikipedia.org/wiki/Сокол_(футбольный_клуб,_Саратов)"
    const val IU_ClubFNL_Chay  = "https://ru.wikipedia.org/wiki/Чайка_(футбольный_клуб,_Песчанокопское)"
    const val IU_ClubFNL_Balt  = "https://ru.wikipedia.org/wiki/Балтика_(футбольный_клуб)"
    const val IU_ClubFNL_Rodn  = "https://ru.wikipedia.org/wiki/Родина_(футбольный_клуб)"
    const val IU_ClubFNL_Alan  = "https://ru.wikipedia.org/wiki/Алания_(футбольный_клуб)"
    const val IU_ClubFNL_Ufau  = "https://ru.wikipedia.org/wiki/Уфа_(футбольный_клуб)"
    const val IU_ClubFNL_Kamz  = "https://ru.wikipedia.org/wiki/КАМАЗ_(футбольный_клуб)"
    const val IU_ClubFNL_Cher  = "https://ru.wikipedia.org/wiki/Черноморец_(футбольный_клуб,_Новороссийск)"
    const val IU_ClubFNL_Shin  = "https://ru.wikipedia.org/wiki/Шинник_(футбольный_клуб)"
    const val IU_ClubFNL_Soci  = "https://ru.wikipedia.org/wiki/Сочи_(футбольный_клуб)"
    const val IU_ClubFNL_Tumn  = "https://ru.wikipedia.org/wiki/Тюмень_(футбольный_клуб)"
    const val IU_ClubFNL_Skah  = "https://ru.wikipedia.org/wiki/СКА-Хабаровск"


    const val IN_Common_HisRPL = "История РПЛ"
    const val IN_Common_HisCup = "История кубка"
    const val IN_Common_SbrRUS = "Сборная России"
    const val IN_Common_SbrUSR = "Сборная СССР"

    const val IP_Common_HisRPL = "logo_rpl"
    const val IP_Common_HisCup = "logo_cuprus"
    const val IP_Common_SbrRUS = "flg_russia"
    const val IP_Common_SbrUSR = "flg_ussr"

    const val IU_Common_HisRPL = "https://ru.wikipedia.org/wiki/Российская_премьер-лига"
    const val IU_Common_HisCup = "https://ru.wikipedia.org/wiki/Кубок_России_по_футболу"
    const val IU_Common_SbrRUS = "https://ru.wikipedia.org/wiki/Сборная_России_по_футболу"
    const val IU_Common_SbrUSR = "https://ru.wikipedia.org/wiki/Сборная_СССР_по_футболу"

}