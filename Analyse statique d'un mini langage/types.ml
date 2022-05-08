(* Module Types *)

type position = int
(** Position : numéro de ligne dans le fichier, débutant à 1 *)

type name = string
(** Nom de variable *)

(** Opérateurs arithmétiques : + - * / % *)
type op = Add | Sub | Mul | Div | Mod

(** Expressions arithmétiques *)
type expr = Num of int | Var of name | Op of op * expr * expr

(** Opérateurs de comparaisons *)
type comp =
  | Eq (* = *)
  | Ne (* Not equal, <> *)
  | Lt (* Less than, < *)
  | Le (* Less or equal, <= *)
  | Gt (* Greater than, > *)
  | Ge
(* Greater or equal, >= *)

type cond = expr * comp * expr
(** Condition : comparaison entre deux expressions *)

(** Instructions *)
type instr =
  | Set of name * expr
  | Read of name
  | Print of expr
  | If of cond * block * block
  | While of cond * block

and block = (position * instr) list

type program = block
(** Un programme Polish est un bloc d'instructions *)

type env = (name * int) list
type sign = Pos | Neg | Zero | Error
type env_sign = (name * sign list) list
