(* Module Simpl - simplifying Polish programs *)

open Types
open Eval
open Print
open Common 

(* Simplification of . + . *)
let simpl_add expr1 expr2 =
  match (expr1, expr2) with
  | Num x, Num y -> Num (x + y)
  | Num 0, expr2 -> expr2
  | expr1, Num 0 -> expr1
  | expr1, Num i -> Op (Add, expr1, Num i)
  | Num i, expr2 -> Op (Add, expr2, Num i)
  | expr1, expr2 -> Op (Add, expr1, expr2)

(* Simplification of . - . *)
let simpl_sub expr1 expr2 =
  match (expr1, expr2) with
  | Num x, Num y -> Num (x - y)
  | expr1, Num 0 -> expr1
  | expr1, Num i -> Op (Sub, expr1, Num i)
  | Num i, expr2 -> Op (Sub, expr2, Num i)
  | expr1, expr2 -> Op (Sub, expr1, expr2)

(* Simplification of . * . *)
let simpl_mul expr1 expr2 =
  match (expr1, expr2) with
  | Num x, Num y -> Num (x * y)
  | Num 0, expr2 -> Num 0
  | expr1, Num 0 -> Num 0
  | Num 1, expr2 -> expr2
  | expr1, Num 1 -> expr1
  | expr1, Num i -> Op (Mul, expr1, Num i)
  | Num i, expr2 -> Op (Mul, expr2, Num i)
  | expr1, expr2 -> Op (Mul, expr1, expr2)

(* Simplification of . / . *)
let simpl_div expr1 expr2 =
  match (expr1, expr2) with
  | Num x, Num y -> Num (x / y)
  | Num 0, expr -> Num 0
  | expr, Num 1 -> expr
  | expr, Num i -> Op (Div, expr, Num i)
  | Num i, expr -> Op (Div, Num i, expr)
  | expr1, expr2 -> Op (Div, expr1, expr2)


(* Simplification of . mod . *)
let simpl_mod expr1 expr2 =
  match (expr1, expr2) with
  | Num x, Num y -> Num (x mod y)
  | Num 0, expr2 -> Num 0
  | expr, Num i -> Op (Mod, expr, Num i)
  | Num i, expr -> Op (Mod, Num i, expr)
  | expr1, expr2 -> Op (Mod, expr1, expr2)


(* Simplification of binops *)
let simpl_op op expr1 expr2 =
  match op with
  | Add -> simpl_add expr1 expr2
  | Sub -> simpl_sub expr1 expr2
  | Mul -> simpl_mul expr1 expr2
  | Div -> simpl_div expr1 expr2
  | Mod -> simpl_mod expr1 expr2

(* Simplification of an expression *)
let rec simpl_expr expr =
  match expr with
  | Num i -> Num i
  | Var s -> Var s
  | Op (op, expr1, expr2) -> (simpl_op op) (simpl_expr expr1) (simpl_expr expr2)


(* Simplification of a condition *)
let simpl_cond cond =
  match cond with
  | expr1, comp, expr2 -> (simpl_expr expr1, comp, simpl_expr expr2)

(* Simplification of an instruction *)
let rec simpl_instr instr env =
  match instr with
  | Set (s, expr) -> Set (s, simpl_expr expr)
  | Read name -> Read name
  | Print expr -> Print (simpl_expr expr)
  | If (cond, block1, block2) -> (
      let cond' = simpl_cond cond in
      match cond' with
      | Num i, comp, Num j ->
          if eval_cond (Num i, comp, Num j) env then
            If ((Num i, comp, Num j), simpl_block block1 env, [])
          else If ((Num i, comp, Num j), [], simpl_block block2 env)
      | _ -> If (cond', simpl_block block1 env, simpl_block block2 env))
  | While (cond, block) -> (
      let cond' = simpl_cond cond in
      match cond' with
      | Num i, comp, Num j ->
          if eval_cond (Num i, comp, Num j) env then
            While ((Num i, comp, Num j), simpl_block block env)
          else While ((Num i, comp, Num j), [])
      | _ -> While (cond', simpl_block block env))

and simpl_block block env =
  match block with
  | (i, instr) :: block' -> (i, simpl_instr instr env) :: simpl_block block' env
  | [] -> []

let simp_polish (p : program) : unit = print_polish (simpl_block p (eval_block p []))  