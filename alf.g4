grammar alf;

program
    : instruction+  EOF
    ;
instruction: simple #InsSimpleRule
    | declare #InsDeclareRule
    | expression #ExpRule
    | asig #AsigRule
    | function #FunctionRule
    | loop #LoopRule
    | struct #StructRule
    ;

struct:  'class' ID ('local' property ';')* 'end' #StructClass ;


loop: 'for' ID 'from' from=INT 'to' to=INT 'start' instruction* 'finish' SEMI #ForSimple
    | 'for' variable=ID 'in' iterator=ID 'start' instruction* 'finish' SEMI #ForExp
    | 'repeat' instruction* 'while' expression SEMI* #Repeat
    | 'while' expression 'start' instruction* 'finish' SEMI #While
;



function: 'function' ID ':' TYPE '=>' value #FunctionNoDef
        | 'function' ID ':' TYPE 'begin' instruction* 'end' SEMI #FunctionDef
        | 'function' ID '(' property (',' property)*? ')' ':' TYPE SEMI #FunctionDefEx
        | 'function'ID '(' property (',' property)*? ')' ':' TYPE 'begin' instruction*  'end' SEMI #FunctionStatements
       
;

property: TYPE ID  #PropertySimple
    | TYPE ID '=' init=expression #PropertyExp
    ;

paralist: 
        TYPE ID ',' paralist #ParaListMul
        |TYPE ID    #ParaListSimple
        ;

simple: FLOAT  SEMI   #SimpleFloat
        | CHAR  SEMI  #SimpeChar
        | BOOL   SEMI    #SimpleBool
        | INT   SEMI    #SimpleInt
        |STRING SEMI     #SimpleString 
        | SYMBOL SEMI #SimpleSymbol
        | EMPTY SEMI #SimpleEmpty
        ;   

asig: ID '=' expression SEMI #Asig1
//    | array '=' expression  #Asig2
;

//array: ID '[' expression ']';

declare: DEC declarecontinue (',' declarecontinue)*? SEMI;

declarecontinue: TYPE ID  #DecSimple
    | TYPE ID '=' init=expression #DecValue
    | ID '=' init=expression  #DecAtrib

;

value: INT           #ValueExpInt
    | FLOAT         #ValueExpFloat
    | ID            #IDExp
    | STRING        #StringExp
    | BOOL          #BoolExp
    ;

expression: left=value OP right=expression #ExpOP
            | left=value OP right=value #ExpOPValue
            | '-' expression        #MinExp
            | value #ExpValue
            | 'exec' ID '(' expression? (',' expression)* ')' SEMI* #ExecFunction
            | 'if' expression ify* ('else' elsey)*? 'end' SEMI* #If
;

ify: instruction;
elsey: instruction;

FROM: 'from'|'in';
DEC: 'declare';
TYPE: 'integer'|'float'|'char'|'boolean'|'string' ;
EMPTY: 'empty';
BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;
SYMBOL:  '"' ~["\\] '"';
SEMI: ';'|',';
OP : '+'|'-'|'*'|'/'|'mod'|'<'|'>'|'==';
STRING : '"'.*?~[\\] '"'| '""';
FLOAT: [0-9]+'.'[0-9]+;
CHAR: '\'' [a-zA-Z0-9] '\'';
BOOL: 'true'|'false';
INT : [0-9]+;
ID: [a-zA-Z_][a-zA-Z_0-9]* ;
ID2: [a-zA-Z_][a-zA-Z_0-9]*;
WS: [ \t\n\r\f]+ -> skip ;
SOMETHING: [a-z]+;
//