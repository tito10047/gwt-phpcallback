Úlohou našej testovacej aplikácie bude registrácia a následné prihlásenie. Databázovú časť nebudem robiť, vystačíme si z dátami v dočasnom poli.
Potrebujeme:
Formulár na registráciu, ktorý bude obsahovať pole na nick, dva polia na heslo a tlačítko na odoslanie.
Formulár na prihlásenie, ten bude obsahovať len pole na nick a heslo.
Objekt Užívateľa, ktorý bude prenášať údaj nicku, a dátume registrácie .
Metódu na registráciu, ktorá bude mať ako argumenty nick a heslo. Návratová hodnota bude objekt registrovaného užívateľa, alebo null ak je registrácia neúspešná. V normálnom prípade sa toto robí cez exception, kedže ten ešte nemám dokončený, vystačíme si z null.
A poslednú metódu na prihlásenie z rovnakými argumentami ako registračná, návratová hodnota však bude objekt užívateľa.

(Java)
Ako prvé si vytvoríme prázdny gwt projekt. Bližší návod ako na to hľadajte v starších príspevkoch.
Do súbora pridáme riadok :
```
<inherits name='com.mostka.phprpc.PhpRpc'/>
```
Defaultne je nastavená koreňová z zložka servera na ´project/war/server´ a hlavný script na ´server/jsonphprpc.config.php´ . V prípade potreby je to možné zmeniť následujúcim spôsobom.
```
<set-configuration-property name="serverPath" value="./server" />
<set-configuration-property name="phpIndexFile" value="jsonphprpc.config.php" />
```
Samozrejme je nutné vložiť knihovnu do projektu. Stiahnuť ku môžete zo stránky projektu.

Ako prvé vytvoríme objekt užívateľa, následne metódy na komunikáciu z serverom.
Trieda User, ako aj všetky ostatné ktoré prenášame na server a späť, musí byť potomkom triedy PhpRpcObject

```
package com.mostka.phprpclib.examples.login.client;

import com.mostka.phprpc.client.PhpRpcObject;
import com.mostka.phprpc.client.PhpRpcRelocatePath;

@PhpRpcRelocatePath("objects")
public class User extends PhpRpcObject{
	public String nick = "";
	public int registredDate = 0;
}
```
Pokiaľ si chceme umiestnenie objektov usporiadať do vlastnej štruktúry, použijeme anotáciu
@PhpRpcRelocatePath("objects") .
Kompilátor jej php verziu  uloží do priečinka ´project/war/server/objects/User.class.php´

Rovnakým spôsobom vytvoríme objekt informácie o úspešnosti registrácie RegisterNotification

```
package com.mostka.phprpclib.examples.login.client;

import com.mostka.phprpc.client.PhpRpcObject;
import com.mostka.phprpc.client.PhpRpcRelocatePath;

@PhpRpcRelocatePath("objects")
public class RegisterNotification  extends PhpRpcObject{
	
	public boolean success = false;
	public boolean userExist = false;
	public boolean unexpectedError = false;
	public String mesage = "";
}
```
Ďalej vytvoríme deklarácie potrebných metód na prihlásenie a registráciu.
```
package com.mostka.phprpclib.examples.login.client;

import com.mostka.phprpc.client.PhpRpcCallback;
import com.mostka.phprpc.client.PhpRpcRelocatePath;
import com.mostka.phprpc.client.PhpRpcService;

@PhpRpcRelocatePath("services")
public class UserService implements PhpRpcService{

	public void registerUser(String nick, String name, PhpRpcCallback<RegisterNotification> p_callBack){};
	public void loginUser(String nick, String name, PhpRpcCallback<User> p_callBack){};
		
}
```
Všetky metódy musia mať návratový typ void a ako posledný argument triedu PhpRpcCallback z definovaným argumentom ktorý reprezentuje návratovú hodnotu metódy. Objekt musí byť potomkom PhpRpcService